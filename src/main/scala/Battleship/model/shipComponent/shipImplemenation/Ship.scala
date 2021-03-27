package Battleship.model.shipComponent.shipImplemenation

import Battleship.model.shipComponent.InterfaceShip

import scala.collection.mutable

case class Ship(shipLength: Int, shipCoordinates: Array[mutable.Map[String, Int]], status: Boolean) extends InterfaceShip {

  def hit(x: Int, y: Int): InterfaceShip = {
    if (coordsExists(x, y)) {
      changeValueToHit(x, y)
      return changeStatusWhenSunk()
    }
    this
  }

  private def coordsExists(x: Int, y: Int): Boolean = {
    shipCoordinates.exists(_.get("x").contains(x)) && shipCoordinates.exists(_.get("y").contains(y))
  }

  private def changeValueToHit(x: Int, y: Int): Unit = {
    shipCoordinates.foreach(coords => if (coords.get("x").contains(x) && coords.get("y").contains(y)) {
      coords("value") = 0
    })
  }

  private def changeStatusWhenSunk(): InterfaceShip = {
    this.copy(status = isSunk)
  }

  private def isSunk: Boolean = {
    !shipCoordinates.exists(_.get("value").contains(1))
  }

}