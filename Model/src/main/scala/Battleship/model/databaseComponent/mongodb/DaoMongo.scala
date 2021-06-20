package Battleship.model.databaseComponent.mongodb

import Battleship.AkkaHttpModel
import Battleship.model.databaseComponent.DaoInterface
import Battleship.model.shipComponent.InterfaceShip
import Battleship.model.shipComponent.shipImplemenation.Ship
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates.set
import org.mongodb.scala.result.{DeleteResult, InsertOneResult, UpdateResult}
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase, Observer, SingleObservable}
import play.api.libs.json.Json

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContextExecutor}

case class DaoMongo() extends DaoInterface {
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  val uri: String = "mongodb://" + sys.env.getOrElse("MONGODB_HOST", "localhost:27017")
  val client: MongoClient = MongoClient(uri)
  val database: MongoDatabase = client.getDatabase("battleship")
  println("Connect to: " + uri)

  val controllerCollection: MongoCollection[Document] = database.getCollection("Controller")
  val playerCollection: MongoCollection[Document] = database.getCollection("Player")
  val shipSetListCollection: MongoCollection[Document] = database.getCollection("ShipSetList")
  val shipListCollection: MongoCollection[Document] = database.getCollection("ShipList")
  val gridCollection: MongoCollection[Document] = database.getCollection("Grid")

  override def create(): Unit = {
    handleObserverInsertion(
      controllerCollection.insertOne(
        Document("_id" -> 1, "gameState" -> "", "playerState" -> "")
      ),
    )
    handleObserverInsertion(
      controllerCollection.insertOne(
        Document("_id" -> 2, "gameState" -> "", "playerState" -> "")
      ),
    )
    handleObserverInsertion(
      playerCollection.insertOne(Document("_id" -> 1, "playerName" -> "")),

    )
    handleObserverInsertion(
      playerCollection.insertOne(Document("_id" -> 2, "playerName" -> "")),
    )
    handleObserverInsertion(
      shipSetListCollection.insertOne(
        Document("_id" -> 1, "shipSetList" -> "")
      ),
    )
    handleObserverInsertion(
      shipSetListCollection.insertOne(
        Document("_id" -> 2, "shipSetList" -> "")
      ),
    )
    handleObserverInsertion(
      gridCollection.insertOne(Document("_id" -> 1, "field" -> "")),
    )
    handleObserverInsertion(
      gridCollection.insertOne(Document("_id" -> 2, "field" -> "")),
    )
  }

  private def handleObserverInsertion(
                                       insertObservable: SingleObservable[InsertOneResult],
                                     ): Unit = {
    insertObservable.subscribe(new Observer[InsertOneResult] {
      override def onNext(result: InsertOneResult): Unit = println(s"inserted: $result")

      override def onError(e: Throwable): Unit = println(s"onError: $e")

      override def onComplete(): Unit = println("completed")
    })
  }

  override def read(id: Int): (
    String,
      Vector[Map[String, Int]],
      Map[String, Int],
      Vector[InterfaceShip],
      String,
      String
    ) = {
    val resultPlayer = Await.result(
      playerCollection.find(equal("_id", id)).first().head(),
      atMost = 10.second
    )
    val playerName = resultPlayer("playerName").asString().getValue
    val resultGrid = Await.result(
      gridCollection.find(equal("_id", id)).first().head(),
      atMost = 10.second
    )
    val grid = Json
      .parse(resultGrid("field").asString().getValue)
      .as[Vector[Map[String, Int]]]
    val resultController = Await.result(
      controllerCollection.find(equal("_id", id)).first().head(),
      atMost = 10.second
    )
    val gameState = resultController("gameState").asString().getValue
    val playerState = resultController("playerState").asString().getValue
    val resultShipSetList = Await.result(
      shipSetListCollection.find(equal("_id", id)).first().head(),
      atMost = 10.second
    )
    val shipSetList = Json
      .parse(resultShipSetList("shipSetList").asString().getValue)
      .as[Map[String, Int]]
    val resultShipList = shipListCollection.find(equal("playerId", id))
    val shipMapping = resultShipList.map(elem =>
      Ship(elem("shipLength").asInt32().getValue, Json.parse(elem("coords").asString().getValue)
          .as[Vector[Map[String, Int]]], elem("status").asBoolean().getValue)
    )
    val shipList = Await.result(shipMapping.toFuture(), atMost = 10.second).toVector
    (playerName, grid, shipSetList, shipList, gameState, playerState)
  }

  override def update(
                       id: Int,
                       playerName: String,
                       grid: Vector[Map[String, Int]],
                       shipSetList: Map[String, Int],
                       shipList: Vector[InterfaceShip],
                       gameState: String,
                       playerState: String
                     ): Unit = {
    handleObserverUpdate(
      controllerCollection
        .updateOne(equal("_id", id), set("gameState", gameState)),
      gameState,
      playerState
    )
    handleObserverUpdate(
      controllerCollection
        .updateOne(equal("_id", id), set("playerState", playerState)),
      gameState,
      playerState
    )
    handleObserverUpdate(
      playerCollection
        .updateOne(equal("_id", id), set("playerName", playerName)),
      gameState,
      playerState
    )
    handleObserverUpdate(
      gridCollection.updateOne(
        equal("_id", id),
        set("field", Json.toJson(grid).toString())
      ),
      gameState,
      playerState
    )
    handleObserverUpdate(
      shipSetListCollection.updateOne(
        equal("_id", id),
        set("shipSetList", Json.toJson(shipSetList).toString())
      ),
      gameState,
      playerState
    )
    settingShipListToDatabase(id, shipList, gameState, playerState)
  }

  private def handleObserverUpdate(
                                    insertObservable: SingleObservable[UpdateResult],
                                    gameState: String,
                                    playerState: String
                                  ): Unit = {
    insertObservable.subscribe(new Observer[UpdateResult] {
      override def onNext(result: UpdateResult): Unit =
        println(s"inserted: $result")

      override def onError(e: Throwable): Unit = {
        println(s"onError: $e")
        AkkaHttpModel.retryWithNewDatabase("mysql", gameState, playerState)
      }

      override def onComplete(): Unit = println("completed")
    })
  }

  private def settingShipListToDatabase(
                                         id: Int,
                                         shipList: Vector[InterfaceShip],
                                         gameState: String,
                                         playerState: String
                                       ): Unit = {
    shipListCollection
      .deleteMany(equal("playerId", id))
      .subscribe(new Observer[DeleteResult] {
        override def onNext(result: DeleteResult): Unit =
          println(s"inserted: $result")

        override def onError(e: Throwable): Unit = {
          println(s"onError: $e")
          AkkaHttpModel.retryWithNewDatabase("mysql", gameState, playerState)
        }

        override def onComplete(): Unit = println("deleted all")
      })
    for (ship <- shipList)
      yield handleObserverInsertion(
        shipListCollection.insertOne(
          Document(
            "playerId" -> id,
            "shipLength" -> ship.shipLength,
            "status" -> ship.status,
            "coords" -> Json.toJson(ship.shipCoordinates).toString()
          )
        )
      )
  }

  override def delete(): Unit = {
    controllerCollection.drop()
    playerCollection.drop()
    gridCollection.drop()
    shipSetListCollection.drop()
    shipListCollection.drop()
  }
}
