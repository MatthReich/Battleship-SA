package Battleship.model.databaseComponent.slick

import slick.jdbc.MySQLProfile.api._

class ShipListTable(tag: Tag) extends Table[(Int, Int, Int, Boolean, Int)](tag, "ShipListTable") {
  override def * = (id, x_value, shipLength, status, shipCoordinatesId)

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def x_value = column[Int]("XValue")

  def shipLength = column[Int]("ShipLength")

  def status = column[Boolean]("Status")

  def shipCoordinatesForeignKey = foreignKey("ShipCoordinates_FK", shipCoordinatesId, TableQuery[ShipCoordinatesTable])(_.id)

  def shipCoordinatesId = column[Int]("ShipCoordinatesId")
}
