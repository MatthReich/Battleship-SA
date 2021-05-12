package Battleship.model.databaseComponent.slick

import slick.jdbc.MySQLProfile.api._

class ControllerTable(tag: Tag) extends Table[(Int, String, String)](tag, "ControllerTable") {
  override def * = (id, gameState, playerState)

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def gameState = column[String]("GameState")

  def playerState = column[String]("PlayerState")
}
