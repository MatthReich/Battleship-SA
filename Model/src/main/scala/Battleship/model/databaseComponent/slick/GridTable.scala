package Battleship.model.databaseComponent.slick

import slick.jdbc.MySQLProfile.api._

class GridTable(tag: Tag) extends Table[(Int, String)](tag, "GridTable") {

  override def * = (id, field)

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def field = column[String]("Field")

}
