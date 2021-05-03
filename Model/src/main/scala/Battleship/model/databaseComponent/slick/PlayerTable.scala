package Battleship.model.databaseComponent.slick

import slick.jdbc.MySQLProfile.api._

class PlayerTable(tag: Tag) extends Table[(Int, String, Int, Int, Int, Int)](tag, "PlayerTable") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("Name")

  def controllerForeignKey = foreignKey("Controller_FK", controllerId, TableQuery[ControllerTable])(_.id)

  def controllerId = column[Int]("Controller")

  def shipListForeignKey = foreignKey("ShipLists_FK", shipListId, TableQuery[ShipListTable])(_.id)

  def shipListId = column[Int]("ShipList")

  def shipSetListForeignKey = foreignKey("ShipSetLists_FK", shipSetListId, TableQuery[ShipSetListTable])(_.id)

  def shipSetListId = column[Int]("ShipSetList")

  def gridForeignKey = foreignKey("Grid_FK", gridId, TableQuery[GridTable])(_.id)

  def gridId = column[Int]("Grid")

  override def * = (id, name, shipSetListId, shipListId, gridId, controllerId)

}
