package Battleship.model.databaseComponent.slick

import slick.jdbc.MySQLProfile.api._

class ShipSetListTable(tag: Tag) extends Table[(Int, String)](tag, "ShipSetListTable") {

  override def * = (id, shipList)

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def shipList = column[String]("ShipList")

}
