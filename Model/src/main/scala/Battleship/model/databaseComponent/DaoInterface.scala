package Battleship.model.databaseComponent

trait DaoInterface {

  def create(): Unit

  def read(id:Int): (String, Vector[Map[String, Int]], Map[String, Int], String, String)

  def update(id: Int, playerName: String, grid: Vector[Map[String, Int]] , shipSetList: Map[String, Int], gameState: String, playerState: String): Unit

  def delete(): Unit
}
