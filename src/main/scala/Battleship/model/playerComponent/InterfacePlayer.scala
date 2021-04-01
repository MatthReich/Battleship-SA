package Battleship.model.playerComponent

import Battleship.model.gridComponent.InterfaceGrid
import Battleship.model.shipComponent.InterfaceShip

trait InterfacePlayer {

  def shipList: Vector[InterfaceShip]

  def grid: InterfaceGrid

  def name: String

  def shipSetList: Map[Int, Int]

  def updateName(input: String): InterfacePlayer

  def updateGrid(grid: InterfaceGrid): InterfacePlayer

  def updateShipSetList(valueIn: Int): InterfacePlayer

  def updateShip(oldShip: InterfaceShip, newShip: InterfaceShip): InterfacePlayer

  def addShip(ship: InterfaceShip): InterfacePlayer
}
