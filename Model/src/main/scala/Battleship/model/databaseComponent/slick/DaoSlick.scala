package Battleship.model.databaseComponent.slick

import Battleship.AkkaHttpModel
import Battleship.model.databaseComponent.DaoInterface
import Battleship.model.shipComponent.InterfaceShip
import Battleship.model.shipComponent.shipImplemenation.Ship
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.adapter._
import akka.pattern.retry
import play.api.libs.json.Json
import slick.jdbc.JdbcBackend
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.MySQLProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success}

case class DaoSlick() extends DaoInterface {

  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
  implicit val scheduler: akka.actor.Scheduler = system.scheduler.toClassic
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  val databaseUrl: String =
    "jdbc:mysql://" + sys.env.getOrElse(
      "DATABASE_HOST",
      "localhost:3306"
    ) + "/" + sys.env.getOrElse(
      "MYSQL_DATABASE",
      "battleship"
    ) + "?serverTimezone=UTC"

  val databaseUser: String     = sys.env.getOrElse("MYSQL_USER", "battleship")
  val databasePassword: String = sys.env.getOrElse("MYSQL_PASSWORD", "battleship")
  println("Connect to: " + databaseUrl)

  val database: JdbcBackend.DatabaseDef = Database.forURL(
    url = databaseUrl,
    user = databaseUser,
    password = databasePassword,
    driver = "com.mysql.cj.jdbc.Driver"
  )

  val playerTable: TableQuery[PlayerTable]                   = TableQuery[PlayerTable]
  val controllerTable: TableQuery[ControllerTable]           = TableQuery[ControllerTable]
  val gridTable: TableQuery[GridTable]                       = TableQuery[GridTable]
  val shipCoordinatesTable: TableQuery[ShipCoordinatesTable] = TableQuery[ShipCoordinatesTable]
  val shipListTable: TableQuery[ShipListTable]               = TableQuery[ShipListTable]
  val shipSetListTable: TableQuery[ShipSetListTable]         = TableQuery[ShipSetListTable]

  val setup = DBIO.seq(
    (
      playerTable.schema
        ++ controllerTable.schema
        ++ gridTable.schema
        ++ shipCoordinatesTable.schema
        ++ shipListTable.schema
        ++ shipSetListTable.schema
    ).createIfNotExists
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

  private def initControllerTable(id: Int, query: TableQuery[ControllerTable]): Unit = {
    val updateQuery = {
      query
        .filter(_.id === id)
        .exists
        .result
        .flatMap(
          exists =>
            if (!exists) {
              query += (id, "", "")
            } else {
              DBIO.successful(None)
            }
        )
        .transactionally
    }
    complete(
      retry(() => (database.run(updateQuery)), attempts = 10, 1.second),
      "while init",
      "",
      ""
    )
  }

  private def initShipSetList(
      id: Int,
      query: TableQuery[ShipSetListTable]
  ): Unit = {
    val updateQuery = {
      query
        .filter(_.id === id)
        .exists
        .result
        .flatMap(
          exists =>
            if (!exists) {
              query += (id, "")
            } else {
              DBIO.successful(None)
            }
        )
        .transactionally
    }
    complete(
      retry(() => (database.run(updateQuery)), attempts = 10, 1.second),
      "while init",
      "",
      ""
    )
  }

  private def initGridTable(id: Int, query: TableQuery[GridTable]): Unit = {
    val updateQuery = {
      query
        .filter(_.id === id)
        .exists
        .result
        .flatMap(
          exists =>
            if (!exists) {
              query += (id, "")
            } else {
              DBIO.successful(None)
            }
        )
        .transactionally
    }
    complete(
      retry(() => (database.run(updateQuery)), attempts = 10, 1.second),
      "while init",
      "",
      ""
    )
  }

  private def initPlayerTable(id: Int, query: TableQuery[PlayerTable]): Unit = {
    val updateQuery = {
      query
        .filter(_.id === id)
        .exists
        .result
        .flatMap(
          exists =>
            if (!exists) {
              query += (id, "", id, id, id, id)
            } else {
              DBIO.successful(None)
            }
        )
        .transactionally
    }
    complete(
      retry(() => (database.run(updateQuery)), attempts = 10, 1.second),
      "while init",
      "",
      ""
    )
  }

  override def read(id: Int): (
      String,
      Vector[Map[String, Int]],
      Map[String, Int],
      Vector[InterfaceShip],
      String,
      String
  ) = {
    val resultPlayerTabel = Await.result(
      database.run(playerTable.filter(_.id === id).result),
      atMost = 10.second
    )
    val playerName        = resultPlayerTabel.head._2
    val resultGrid        = Await.result(
      database.run(gridTable.filter(_.id === id).result),
      atMost = 10.second
    )
    val grid              = Json.parse(resultGrid.head._2).as[Vector[Map[String, Int]]]
    val resultController  = Await.result(
      database.run(controllerTable.filter(_.id === id).result),
      atMost = 10.second
    )
    val gameState         = resultController.head._2
    val playerState       = resultController.head._3
    val resultShipSetList = Await.result(
      database.run(shipSetListTable.filter(_.id === id).result),
      atMost = 10.second
    )
    val shipSetList       = Json.parse(resultShipSetList.head._2).as[Map[String, Int]]
    val resultShipList    = Await.result(
      database.run(shipListTable.filter(_.playerId === id).result),
      atMost = 10.second
    )
    val shipList          = recreateShips(resultShipList)
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
    complete(
      retry(
        () =>
          database.run(
            playerTable.filter(_.id === id).update((id, playerName, id, id, id, id))
          ),
        attempts = 10,
        200 milliseconds
      ),
      "player Player_0" + id,
      gameState,
      playerState
    )
    complete(
      retry(
        () =>
          database.run(
            gridTable.filter(_.id === id).update((id, Json.toJson(grid).toString()))
          ),
        attempts = 10,
        200 milliseconds
      ),
      "grid Player_0" + id,
      gameState,
      playerState
    )
    complete(
      retry(
        () =>
          database.run(
            controllerTable.filter(_.id === id).update((id, gameState, playerState))
          ),
        attempts = 10,
        200 milliseconds
      ),
      "controller states",
      gameState,
      playerState
    )
    complete(
      retry(
        () =>
          database.run(
            shipSetListTable.filter(_.id === id).update(id, Json.toJson(shipSetList).toString())
          ),
        attempts = 10,
        200 milliseconds
      ),
      "shipSetList Player_0" + id,
      gameState,
      playerState
    )
    complete(
      retry(
        () => database.run(shipListTable.filter(_.playerId === id).delete),
        attempts = 10,
        200 milliseconds
      ),
      "shipList Player_0" + id,
      gameState,
      playerState
    )
    for (ship <- shipList)
      yield complete(
        retry(
          () =>
            database.run(
              shipListTable += (0, id, ship.shipLength, ship.status, Json
                .toJson(ship.shipCoordinates)
                .toString())
            ),
          attempts = 10,
          200 milliseconds
        ),
        "shipList Player_0" + id,
        gameState,
        playerState
      )
  }

  private def complete(
      f: Future[_],
      msg: String,
      gameState: String,
      playerState: String
  ): Unit = {
    f.onComplete {
      case Success(_)         =>  println("saved: " + msg)
      case Failure(exception) =>
        println("Error while saving " + msg + ": " + exception.getMessage)
        AkkaHttpModel.retryWithNewDatabase("mongodb", gameState, playerState)
    }
  }

  override def delete(): Unit = {
    playerTable.delete
    gridTable.delete
    shipSetListTable.delete
    controllerTable.delete
    shipListTable.delete
  }

  private def recreateShips(shipSeq: Seq[(Int, Int, Int, Boolean, String)]): Vector[InterfaceShip] = {
    recreateShipsRec(shipSeq, 0, Vector[InterfaceShip]())
  }

  private def recreateShipsRec(
      shipSeq: Seq[(Int, Int, Int, Boolean, String)],
      index: Int,
      result: Vector[InterfaceShip]
  ): Vector[InterfaceShip] = {
    if (shipSeq.length == index) {
      result
    } else if (shipSeq(index)._5 == "") {
      recreateShipsRec(shipSeq, index + 1, result)
    } else {
      recreateShipsRec(
        shipSeq,
        index + 1,
        result.appended(
          Ship(
            shipSeq(index)._3,
            Json.parse(shipSeq(index)._5).as[Vector[Map[String, Int]]],
            shipSeq(index)._4
          )
        )
      )
    }
  }

}
