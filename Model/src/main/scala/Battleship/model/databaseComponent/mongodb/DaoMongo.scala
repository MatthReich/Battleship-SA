package Battleship.model.databaseComponent.mongodb

import Battleship.model.databaseComponent.DaoInterface
import Battleship.model.shipComponent.InterfaceShip
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates.set
import org.mongodb.scala.result.{InsertOneResult, UpdateResult}
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase, Observable, Observer, SingleObservable}

case class DaoMongo() extends DaoInterface {

  val uri: String = "mongodb://" + sys.env.getOrElse("MONGODB_HOST", "localhost:27017")
  val client: MongoClient = MongoClient(uri)
  val database: MongoDatabase = client.getDatabase("battleship")
  val controllerCollection: MongoCollection[Document] = database.getCollection("Controller")


  override def create(): Unit = {
    controllerCollection.insertOne(Document("_id" -> 1, "gameState" -> "", "playerState" -> ""))
    controllerCollection.insertOne(Document("_id" -> 2, "gameState" -> "", "playerState" -> ""))
  }

  override def read(id: Int): (String, Vector[Map[String, Int]], Map[String, Int], Vector[InterfaceShip], String, String) = ???

  override def update(id: Int, playerName: String, grid: Vector[Map[String, Int]], shipSetList: Map[String, Int], shipList: Vector[InterfaceShip], gameState: String, playerState: String): Unit = {
    handleObserver(controllerCollection.updateMany(equal("_id", id), set("gameState", gameState)))
    handleObserver(controllerCollection.updateMany(equal("_id", id), set("playerState", playerState)))

  }

  private def handleObserver(insertObservable: SingleObservable[UpdateResult]): Unit = {
    insertObservable.subscribe(new Observer[UpdateResult] {
      override def onNext(result: UpdateResult): Unit = println(s"inserted: $result")
      override def onError(e: Throwable): Unit = println(s"onError: $e")
      override def onComplete(): Unit = println("completed")
    })
  }


  override def delete(): Unit = {
    controllerCollection.drop()
  }
}
