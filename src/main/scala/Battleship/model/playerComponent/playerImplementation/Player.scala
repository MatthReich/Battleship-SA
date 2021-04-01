package Battleship.model.playerComponent.playerImplementation

import Battleship.model.gridComponent.InterfaceGrid
import Battleship.model.playerComponent.InterfacePlayer
import Battleship.model.shipComponent.InterfaceShip
import com.google.inject.Inject

case class Player @Inject()(name: String, shipSetList: Map[Int, Int], shipList: Vector[InterfaceShip], grid: InterfaceGrid) extends InterfacePlayer {

  override def updateName(input: String): InterfacePlayer = {
    this.copy(name = input)
  }

  override def updateGrid(newGrid: InterfaceGrid): InterfacePlayer = {
    this.copy(grid = newGrid)
  }

  override def addShip(ship: InterfaceShip): InterfacePlayer = {
    this.copy(shipList = shipList.appended(ship))
  }

  override def updateShipSetList(valueIn: Int): InterfacePlayer = {
    val actualValue = shipSetList.get(valueIn)
    actualValue match {
      case Some(value) =>
        val newVal: Int = value - 1
        this.copy(shipSetList = shipSetList.updated(valueIn, newVal))
      case None =>
        this
    }
  }

  override def updateShip(oldShip: InterfaceShip, ship: InterfaceShip): InterfacePlayer = {
    this.copy(shipList = shipList.updated(shipList.indexOf(oldShip), ship))
  }

}
