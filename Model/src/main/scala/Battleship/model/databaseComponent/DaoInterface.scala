package Battleship.model.databaseComponent

trait DaoInterface {

  def create(): Unit

  def read(): (String, String)

  def update(id: Int, playerName: String, grid: Vector[Map[String, Int]] , shipSetList: Map[String, Int], gameState: String, playerState: String): Unit

  def delete(): Unit
}
