package Battleship.model.databaseComponent.slick

import slick.jdbc.MySQLProfile.api._

class ShipSetListTable(tag: Tag) extends Table[(Int, Int, Int)](tag, "ShipSetListTable") {

  override def * = (id, length, remaining)

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def length = column[Int]("ShipLength")

  def remaining = column[Int]("Remaining")

}
