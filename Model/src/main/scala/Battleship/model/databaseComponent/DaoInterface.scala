package Battleship.model.databaseComponent

import Battleship.model.shipComponent.InterfaceShip

trait DaoInterface {

  def create(): Unit

  def read(id: Int): (String, Vector[Map[String, Int]], Map[String, Int], Vector[InterfaceShip], String, String)

  def update(id: Int, playerName: String, grid: Vector[Map[String, Int]], shipSetList: Map[String, Int], shipList: Vector[InterfaceShip], gameState: String, playerState: String): Unit

  def delete(): Unit

}
