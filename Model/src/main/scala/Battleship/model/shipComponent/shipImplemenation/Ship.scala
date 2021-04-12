package Battleship.model.shipComponent.shipImplemenation

import Battleship.model.shipComponent.InterfaceShip
import com.google.inject.Inject

import scala.util.{Failure, Success, Try}

case class Ship @Inject()(shipLength: Int, shipCoordinates: Vector[Map[String, Int]], status: Boolean) extends InterfaceShip {

  private val fieldIsNotHit: Int = 1
  private val fieldIsHit: Int = 0

  override def hit(x: Int, y: Int): Try[InterfaceShip] = {
    val retVal = changedValue(x, y)
    retVal match {
      case Success(value) => Success(this.copy(shipCoordinates = value, status = isSunk(value)))
      case Failure(exception) => Failure(exception)
    }
  }

  private def changedValue(x: Int, y: Int): Try[Vector[Map[String, Int]]] = {
    val index = shipCoordinates.indexWhere(mapping => mapping.get("x").contains(x) && mapping.get("y").contains(y))
    Try(shipCoordinates.updated(index, shipCoordinates(index) + ("value" -> fieldIsHit))) match {
      case Failure(_) => Failure(new Exception("failed to hit a ship"))
      case Success(value) => Success(value)
    }
  }

  private def isSunk(value: Vector[Map[String, Int]]): Boolean = !value.exists(_.get("value").contains(fieldIsNotHit))

}
