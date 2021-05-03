package Battleship.model.databaseComponent

trait DaoInterface {

  def create(): Unit

  def read(): (String, String)

  def update(gameState: String, playerState: String): Unit

  def delete(): Unit
}
