package Battleship.model.gridComponent.gridImplementation

import Battleship.model.gridComponent.{InterfaceGrid, InterfaceStrategyCollide}
import Battleship.model.shipComponent.shipImplemenation.Ship

import scala.collection.mutable.ListBuffer

case class Grid(size: Int, var listOfShips: ListBuffer[Ship], strategyCollide: InterfaceStrategyCollide) extends InterfaceGrid {
  private var field = Array.ofDim[Int](size, size)


  def addShip(ship: Ship): Boolean = {
    if (!strategyCollide.collide(ship, this)) {
      update()
      return true
    }
    false
  }

  def getShip(x: Int, y: Int): Ship = {
    listOfShips.foreach(shipIterate => shipIterate.shipCoordinates.foreach(coords => if (coords.get("x").contains(x) && coords.get("y").contains(y)) {
      return shipIterate
    }))
    null
  }

  private def update(): Unit = {
    // GridChange Event
  }

  def winStatement(): Boolean = {
    listOfShips.nonEmpty && listOfShips.exists(_.status == false)
  }

}
