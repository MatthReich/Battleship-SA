package Battleship.model.databaseComponent.slick

import Battleship.model.databaseComponent.DaoInterface
import Battleship.model.playerComponent.InterfacePlayer
import slick.jdbc.JdbcBackend.Database
import slick.lifted.TableQuery
import slick.jdbc.MySQLProfile.api._

case class DaoSlick() extends DaoInterface {

  val databaseUrl: String = "jdbc:mysql://" + sys.env.getOrElse("DATABASEHOST", "localhost:3306") + "/" + sys.env.getOrElse("MYSQL_DATABASE", "battleship") + "?serverTimezone=UTC"
  val databaseUser: String = sys.env.getOrElse("MYSQL_USER", "battleship")
  val databasePassword: String = sys.env.getOrElse("MYSQL_PASSWORD", "battleship")

  val database = Database.forURL(
    url = databaseUrl,
    driver = "com.mysql.cj.jdbc.Driver",
    user = databaseUser,
    password = databasePassword
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
    ).createIfNotExists)
  database.run(setup)

  override def load(): (String, String) = {
    ("", "")
  }

  override def save(gameState: String, playerState: String, player: InterfacePlayer): Unit = {

  }
}
