package Battleship.model.shipComponent.shipImplementation

import Battleship.model.shipComponent.ShipInterface
import scala.util.Try
import scala.util.Success
import scala.util.Failure

case class Ship(shipLength: Int, shipCoordinates: Vector[Map[String, Int]], status: Boolean) extends ShipInterface:
    private val fieldIsNotHit: Int = 1
    private val fieldIsHit: Int    = 0

    override def hit(x: Int, y: Int): Try[ShipInterface]         = changedValue(x, y) match
        case Failure(exception) => Failure(exception)
        case Success(value)     => Success(this.copy(shipCoordinates = value, status = isSunk(value)))

    private def changedValue(x: Int, y: Int): Try[Vector[Map[String, Int]]] =
        val index = shipCoordinates.indexWhere(mapping => mapping.get("x").contains(x) && mapping.get("y").contains(y))
        Try(shipCoordinates.updated(index, shipCoordinates(index) + ("value" -> fieldIsHit))) match
            case Failure(_)     => Failure(new Exception("failed to hit a ship"))
            case Success(value) => Success(value)

    private def isSunk(value: Vector[Map[String, Int]]): Boolean = !value.exists(_.get("value").contains(fieldIsNotHit))
