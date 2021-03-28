package Battleship.model.playerComponent.playerImplementation

import Battleship.model.gridComponent.InterfaceGrid
import Battleship.model.playerComponent.InterfacePlayer
import Battleship.model.shipComponent.InterfaceShip

import scala.collection.mutable.ListBuffer

case class Player(name: String, shipList: ListBuffer[InterfaceShip], grid: InterfaceGrid) extends InterfacePlayer {

}
