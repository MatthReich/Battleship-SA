package Battleship.model.databaseComponent.slick

import Battleship.model.databaseComponent.DaoInterface
import slick.jdbc.JdbcBackend.Database
import slick.lifted.TableQuery
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, DurationInt}

case class DaoSlick() extends DaoInterface {

  val databaseUrl: String = "jdbc:mysql://" + sys.env.getOrElse("DATABASE_HOST", "localhost:3306") + "/" + sys.env.getOrElse("MYSQL_DATABASE", "battleship") + "?serverTimezone=UTC"
  val databaseUser: String = sys.env.getOrElse("MYSQL_USER", "battleship")
  val databasePassword: String = sys.env.getOrElse("MYSQL_PASSWORD", "battleship")
  println("Connect to: " + databaseUrl)

  val database = Database.forURL(
    url = databaseUrl,
    user = databaseUser,
    password = databasePassword,
    driver = "com.mysql.cj.jdbc.Driver",
  )

  val playerTable = TableQuery[PlayerTable]
  val controllerTable = TableQuery[ControllerTable]
  val gridTable = TableQuery[GridTable]
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
  }

  override def read(): (String, String) = {
    val query = playerTable.take(0).result
    val result = Await.result(database.run(query), atMost = 10.second)
    println(result.head._1)
    ("", "")
  }

  override def update(gameState: String, playerState: String): Unit = {
    Await.result(database.run(playerTable += (0, "Marcel", 0, 0, 0, 0)), Duration.Inf)
    println("First save")
  }

  override def delete(): Unit = {

  }
}
