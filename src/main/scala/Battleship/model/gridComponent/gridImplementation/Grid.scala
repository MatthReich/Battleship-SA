package Battleship.model.gridComponent.gridImplementation

import Battleship.model.gridComponent.{InterfaceGrid, InterfaceStrategyCollide}
import Battleship.model.shipComponent.shipImplemenation.Ship

import scala.collection.mutable.ListBuffer

class Grid(val size: Int, var listOfShips: ListBuffer[Ship], val strategyCollide: InterfaceStrategyCollide) extends InterfaceGrid {
  private var field = Array.ofDim[Int](size, size)


  def addShip(ship: Ship): Boolean = {
    if (!strategyCollide.collide(ship, this)) {
      update()
      return true
    }
    false
  }

  def getShip(x: Int, y: Int): Ship = {
    var ship = new Ship(null, null, null)
    listOfShips.foreach(shipIterate => shipIterate.shipCoordinates.foreach(coords => if(coords.get("x").contains(x) && coords.get("y").contains(y)) {ship = shipIterate}))
    ship
  }

  private def update(): Unit = {

    // GridChange Event
  }

  def winStatement(): Boolean = {
    listOfShips.nonEmpty && listOfShips.exists(_.status == false)
  }

}
