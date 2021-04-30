package Battleship.model.databaseComponent.slick

import slick.jdbc.MySQLProfile.api._

class ShipCoordinatesTable(tag: Tag) extends Table[(Int, Int, Int)](tag, "ShipCoordinatesTable") {

  override def * = (id, x_value, y_value)

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def x_value = column[Int]("XValue")

  def y_value = column[Int]("YValue")


}
