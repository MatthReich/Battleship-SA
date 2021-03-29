package Battleship.model.playerComponent

import Battleship.model.gridComponent.InterfaceGrid
import Battleship.model.shipComponent.InterfaceShip

import scala.collection.mutable.ListBuffer

trait InterfacePlayer {

  def shipList: ListBuffer[InterfaceShip]

  def grid: InterfaceGrid

  def name: String

  def updateName(input: String): InterfacePlayer

  def updateGrid(grid: InterfaceGrid): InterfacePlayer

  def updateShip(idx: Int, ship: InterfaceShip): InterfacePlayer

  def addShip(ship: InterfaceShip): InterfacePlayer
}
