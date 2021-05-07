package Battleship.model.databaseComponent.mongodb

import Battleship.model.databaseComponent.DaoInterface
import Battleship.model.shipComponent.InterfaceShip
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates.set
import org.mongodb.scala.result.{DeleteResult, InsertOneResult, UpdateResult}
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase, Observer, SingleObservable}
import play.api.libs.json.Json

case class DaoMongo() extends DaoInterface {

  val uri: String = "mongodb://" + sys.env.getOrElse("MONGODB_HOST", "localhost:27017")
  val client: MongoClient = MongoClient(uri)
  val database: MongoDatabase = client.getDatabase("battleship")
  val controllerCollection: MongoCollection[Document] = database.getCollection("Controller")
  val playerCollection: MongoCollection[Document] = database.getCollection("Player")
  val shipSetListCollection: MongoCollection[Document] = database.getCollection("ShipSetList")
  val shipListCollection: MongoCollection[Document] = database.getCollection("ShipList")
  val gridCollection: MongoCollection[Document] = database.getCollection("Grid")


  override def create(): Unit = {
    handleObserverInsertion(controllerCollection.insertOne(Document("_id" -> 1, "gameState" -> "", "playerState" -> "")))
    handleObserverInsertion(controllerCollection.insertOne(Document("_id" -> 2, "gameState" -> "", "playerState" -> "")))
    handleObserverInsertion(playerCollection.insertOne(Document("_id" -> 1, "playerName" -> "")))
    handleObserverInsertion(playerCollection.insertOne(Document("_id" -> 2, "playerName" -> "")))
    handleObserverInsertion(shipSetListCollection.insertOne(Document("_id" -> 1, "shipSetList" -> "")))
    handleObserverInsertion(shipSetListCollection.insertOne(Document("_id" -> 2, "shipSetList" -> "")))
    handleObserverInsertion(gridCollection.insertOne(Document("_id" -> 1, "field" -> "")))
    handleObserverInsertion(gridCollection.insertOne(Document("_id" -> 2, "field" -> "")))
  }

  override def read(id: Int): (String, Vector[Map[String, Int]], Map[String, Int], Vector[InterfaceShip], String, String) = ???

  override def update(id: Int, playerName: String, grid: Vector[Map[String, Int]], shipSetList: Map[String, Int], shipList: Vector[InterfaceShip], gameState: String, playerState: String): Unit = {
    handleObserverUpdate(controllerCollection.updateOne(equal("_id", id), set("gameState", gameState)))
    handleObserverUpdate(controllerCollection.updateOne(equal("_id", id), set("playerState", playerState)))
    handleObserverUpdate(playerCollection.updateOne(equal("_id", id), set("playerName", playerName)))
    handleObserverUpdate(gridCollection.updateOne(equal("_id", id), set("field", Json.toJson(grid).toString())))
    handleObserverUpdate(shipSetListCollection.updateOne(equal("_id", id), set("shipSetList", Json.toJson(shipSetList).toString())))
    settingShipListToDatabase(id, shipList)
  }

  private def handleObserverInsertion(insertObservable: SingleObservable[InsertOneResult]): Unit = {
    insertObservable.subscribe(new Observer[InsertOneResult] {
      override def onNext(result: InsertOneResult): Unit = println(s"inserted: $result")

      override def onError(e: Throwable): Unit = println(s"onError: $e")

      override def onComplete(): Unit = println("completed")
    })
  }

  private def handleObserverUpdate(insertObservable: SingleObservable[UpdateResult]): Unit = {
    insertObservable.subscribe(new Observer[UpdateResult] {
      override def onNext(result: UpdateResult): Unit = println(s"inserted: $result")

      override def onError(e: Throwable): Unit = println(s"onError: $e")

      override def onComplete(): Unit = println("completed")
    })
  }

  private def settingShipListToDatabase(id: Int, shipList: Vector[InterfaceShip]): Unit = {
    shipListCollection.deleteMany(equal("playerId", id)).subscribe(new Observer[DeleteResult] {
      override def onNext(result: DeleteResult): Unit = println(s"inserted: $result")

      override def onError(e: Throwable): Unit = println(s"onError: $e")

      override def onComplete(): Unit = println("deleted all")
    })
    for (ship <- shipList) yield handleObserverInsertion(shipListCollection.insertOne(Document("playerId" -> id, "shipLength" -> ship.shipLength, "status" -> ship.status, "coords" -> Json.toJson(ship.shipCoordinates).toString())))
  }

  override def delete(): Unit = {
    controllerCollection.drop()
    playerCollection.drop()
    gridCollection.drop()
    shipSetListCollection.drop()
    shipListCollection.drop()
  }
}
