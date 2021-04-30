package Battleship.model.databaseComponent.slick

import slick.jdbc.MySQLProfile.api._

class GridTable(tag: Tag) extends Table[(Int, Int, Int, Int, Int)](tag, "GridTable") {

  override def * = (id, field, x_value, y_value, value)

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def field = column[Int]("Field")

  def x_value = column[Int]("XValue")

  def y_value = column[Int]("YValue")

  def value = column[Int]("Value")
}
