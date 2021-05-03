package Battleship.model.databaseComponent.slick

import Battleship.AkkaHttpModel.{dataBase, grid_player_01, player_01}
import Battleship.model.databaseComponent.DaoInterface
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import play.api.libs.json.Json
import slick.jdbc.JdbcBackend
import slick.jdbc.JdbcBackend.Database
import slick.lifted.TableQuery
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.concurrent.duration.{Duration, DurationInt}

case class DaoSlick() extends DaoInterface {
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext
  val databaseUrl: String = "jdbc:mysql://" + sys.env.getOrElse("DATABASE_HOST", "localhost:3306") + "/" + sys.env.getOrElse("MYSQL_DATABASE", "battleship") + "?serverTimezone=UTC"
  val databaseUser: String = sys.env.getOrElse("MYSQL_USER", "battleship")
  val databasePassword: String = sys.env.getOrElse("MYSQL_PASSWORD", "battleship")
  println("Connect to: " + databaseUrl)

  val database: JdbcBackend.DatabaseDef = Database.forURL(
    url = databaseUrl,
    user = databaseUser,
    password = databasePassword,
    driver = "com.mysql.cj.jdbc.Driver",
  )

  val playerTable = TableQuery[PlayerTable]
  val controllerTable = TableQuery[ControllerTable]
  val gridTable: TableQuery[GridTable] = TableQuery[GridTable]
  val shipCoordinatesTable = TableQuery[ShipCoordinatesTable]
  val shipListTable = TableQuery[ShipListTable]
  val shipSetListTable = TableQuery[ShipSetListTable]

  val setup = DBIO.seq((
    playerTable.schema
      ++ controllerTable.schema
      ++ gridTable.schema
      ++ shipCoordinatesTable.schema
      ++ shipListTable.schema
      ++ shipSetListTable.schema
    ).createIfNotExists,
  )

  override def create(): Unit = {
    Await.result(database.run(setup), atMost = 10.second)

    initPlayerTable(1, playerTable)
    initPlayerTable(2, playerTable)
    initGridTable(1, gridTable)
    initGridTable(2, gridTable)
    initShipSetList(1, shipSetListTable)
    initShipSetList(2, shipSetListTable)
    initControllerTable(1, controllerTable)
    initControllerTable(2, controllerTable)
  }

  override def read(id: Int): (String, Vector[Map[String, Int]], Map[String, Int], String, String) = {
    val resultPlayerTabel = Await.result(database.run(playerTable.filter(_.id === id).result), atMost = 10.second)
    val playerName = resultPlayerTabel.head._2
    val resultGrid = Await.result(database.run(gridTable.filter(_.id === id).result), atMost = 10.second)
    val grid = Json.parse(resultGrid.head._2).as[Vector[Map[String, Int]]]
    val resultController = Await.result(database.run(controllerTable.filter(_.id === id).result), atMost = 10.second)
    val gameState = resultController.head._2
    val playerState = resultController.head._3
    val resultShipSetList = Await.result(database.run(shipSetListTable.filter(_.id === id).result), atMost = 10.second)
    val shipSetList = Json.parse(resultShipSetList.head._2).as[Map[String, Int]]
    (playerName, grid, shipSetList, gameState, playerState)
  }

  override def update(id: Int, playerName: String, grid: Vector[Map[String, Int]], shipSetList: Map[String, Int], gameState: String, playerState: String): Unit = {
    database.run(playerTable.filter(_.id === id).update((id, playerName, id, id, id, id)))
    database.run(gridTable.filter(_.id === id).update((id, Json.toJson(grid).toString())))
    database.run(controllerTable.filter(_.id === id).update((id, gameState, playerState)))
    database.run(shipSetListTable.filter(_.id === id).update(id, Json.toJson(shipSetList).toString()))
  }

  override def delete(): Unit = {

  }

  private def initControllerTable(id: Int, query: TableQuery[ControllerTable]): Unit = {
    val updateQuery = {
      query.filter(_.id === id).exists.result.flatMap(exists =>
        if (!exists) {
          query += (id, "", "")
        } else {
          DBIO.successful(None)
        }
      ).transactionally
    }
    Await.result(database.run(updateQuery), atMost = 10.second)
  }

  private def initShipSetList(id: Int, query: TableQuery[ShipSetListTable]): Unit = {
    val updateQuery = {
      query.filter(_.id === id).exists.result.flatMap(exists =>
        if (!exists) {
          query += (id, "")
        } else {
          DBIO.successful(None)
        }
      ).transactionally
    }
    Await.result(database.run(updateQuery), atMost = 10.second)
  }

  private def initGridTable(id: Int, query: TableQuery[GridTable]): Unit = {
    val updateQuery = {
      query.filter(_.id === id).exists.result.flatMap(exists =>
        if (!exists) {
          query += (id, "")
        } else {
          DBIO.successful(None)
        }
      ).transactionally
    }
    Await.result(database.run(updateQuery), atMost = 10.second)
  }

  private def initPlayerTable(id: Int, query: TableQuery[PlayerTable]): Unit = {
    val updateQuery = {
      query.filter(_.id === id).exists.result.flatMap(exists =>
        if (!exists) {
          query += (id, "", id, id, id, id)
        } else {
          DBIO.successful(None)
        }
      ).transactionally
    }
    Await.result(database.run(updateQuery), atMost = 10.second)
  }

}
