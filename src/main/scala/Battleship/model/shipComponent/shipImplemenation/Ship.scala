package Battleship.model.shipComponent.shipImplemenation

import Battleship.model.shipComponent.InterfaceShip

class Ship(val shipLength: Int, var shipCoordinates: Array[scala.collection.mutable.Map[String, Int]], var status: Boolean) extends InterfaceShip {

  def hit(x: Int, y: Int): Boolean = {
    if (coordsExists(x, y)) {
      changeValueToHit(x, y)
      changeStatusWhenSunk()
      return true
    }
    false
  }

  private def coordsExists(x: Int, y: Int): Boolean = {
    shipCoordinates.exists(_.get("x").contains(x)) && shipCoordinates.exists(_.get("y").contains(y))
  }

  private def changeValueToHit(x: Int, y: Int): Unit = {
    shipCoordinates.foreach(coords => if(coords.get("x").contains(x) && coords.get("y").contains(y)) { coords ("value") = 0 })
  }

  private def changeStatusWhenSunk(): Unit = {
    if (isSunk) status = true
  }

  private def isSunk: Boolean = {
    !shipCoordinates.exists(_.get("value").contains(1))
  }

}
