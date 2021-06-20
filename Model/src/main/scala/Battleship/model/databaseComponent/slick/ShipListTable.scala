package Battleship.model.databaseComponent.slick

import slick.jdbc.MySQLProfile.api._

class ShipListTable(tag: Tag) extends Table[(Int, Int, Int, Boolean, String)](tag, "ShipListTable") {

  def shipCoordinatesId = column[Int]("ShipCoordinatesId")

  override def * = (id, playerId, shipLength, status, coords)

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def playerId = column[Int]("PlayerId")

  def shipLength = column[Int]("ShipLength")

  def status = column[Boolean]("Status")

  def coords = column[String]("Coords")
}
