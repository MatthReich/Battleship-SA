package Battleship.model.playerComponent.playerImplementation

import Battleship.model.gridComponent.InterfaceGrid
import Battleship.model.playerComponent.InterfacePlayer
import Battleship.model.shipComponent.InterfaceShip

import scala.collection.mutable.ListBuffer

case class Player(name: String, shipSetList: Map[Int, Int], shipList: ListBuffer[InterfaceShip], grid: InterfaceGrid) extends InterfacePlayer {

  override def updateName(input: String): InterfacePlayer = {
    this.copy(name = input)
  }

  override def updateGrid(newGrid: InterfaceGrid): InterfacePlayer = {
    this.copy(grid = newGrid)
  }

  override def addShip(ship: InterfaceShip): InterfacePlayer = {
    this.copy(shipList = shipList.addOne(ship))
  }

  override def updateShipSetList(valueIn: Int): InterfacePlayer = {
    val actualValue = shipSetList.get(valueIn)
    actualValue match {
      case Some(value) =>
        var newVal = value
        newVal -= 1
        this.copy(shipSetList = shipSetList.updated(valueIn, newVal))
      case None =>
        this
    }
  }

  override def updateShip(idx: Int, ship: InterfaceShip): InterfacePlayer = {
    shipList.update(idx, ship)
    this
  }

}
