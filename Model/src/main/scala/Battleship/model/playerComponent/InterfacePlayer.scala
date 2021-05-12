package Battleship.model.playerComponent

import Battleship.model.gridComponent.InterfaceGrid
import Battleship.model.shipComponent.InterfaceShip

trait InterfacePlayer {

  def shipList: Vector[InterfaceShip]

  def grid: InterfaceGrid

  def name: String

  def shipSetList: Map[String, Int]

  def updateName(input: String): InterfacePlayer

  def updateGrid(grid: InterfaceGrid): InterfacePlayer

  def updateShipSetList(newShipSetList: Map[String, Int]): InterfacePlayer

  def updateShipSetList(valueIn: Int): InterfacePlayer

  def updateShip(newShipList: Vector[InterfaceShip]): InterfacePlayer

  def updateShip(oldShip: InterfaceShip, newShip: InterfaceShip): InterfacePlayer

  def addShip(ship: InterfaceShip): InterfacePlayer
}
