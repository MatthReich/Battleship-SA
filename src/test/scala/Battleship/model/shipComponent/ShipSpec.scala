package Battleship.model.shipComponent

import Battleship.model.shipComponent.shipImplemenation.Ship
import org.scalatest.wordspec.AnyWordSpec

import scala.util.{Failure, Success}

class ShipSpec extends AnyWordSpec {

  val shipArray: Vector[Map[String, Int]] = Vector(
    Map("x" -> 0, "y" -> 0, "value" -> 1),
    Map("x" -> 0, "y" -> 1, "value" -> 1),
    Map("x" -> 0, "y" -> 2, "value" -> 1)
  )
  private val fieldIsNotHit: Int = 1

  val shipLength = 3
  private val fieldIsHit: Int = 0
  val status = false

  "A Ship" when {

    var ship: InterfaceShip = Ship(shipLength, shipArray, status)

    "new" should {
      "have the correct size" in {
        assert(ship.shipLength === shipLength)
      }

      "have the correct array for the whole length" in {
        assert(ship.shipCoordinates(0) get "value" contains fieldIsNotHit)
        assert(ship.shipCoordinates(1) get "value" contains fieldIsNotHit)
        assert(ship.shipCoordinates(2) get "value" contains fieldIsNotHit)
      }

      "have a status which is false" in {
        assert(ship.status === false)
      }
    }

    "get hit" should {
      "change value of hit field" in {
        ship.hit(0, 0) match {
          case Success(newShip) =>
            ship = newShip
            assert(ship.shipCoordinates(0) get "value" contains fieldIsHit)
            assert(ship.shipCoordinates(1) get "value" contains fieldIsNotHit)
            assert(ship.shipCoordinates(2) get "value" contains fieldIsNotHit)
            assert(ship.status === false)
          case _ => fail("should work, but didnt though")
        }

      }

      "not sunk when a second hit but three can be taken" in {
        ship.hit(0, 1) match {
          case Success(newShip) =>
            ship = newShip
            assert(ship.shipCoordinates(0) get "value" contains fieldIsHit)
            assert(ship.shipCoordinates(1) get "value" contains fieldIsHit)
            assert(ship.shipCoordinates(2) get "value" contains fieldIsNotHit)
            assert(ship.status === false)
          case _ => fail("should work, but didnt though")
        }
      }

      "return false when wrong coordinates are given and not change any value" in {
        ship.hit(7, 11) match {
          case Success(_) => fail("should not change something")
          case Failure(exception) =>
            assert(exception.getMessage == "failed to hit a ship")
            assert(ship.shipCoordinates(0) get "value" contains fieldIsHit)
            assert(ship.shipCoordinates(1) get "value" contains fieldIsHit)
            assert(ship.shipCoordinates(2) get "value" contains fieldIsNotHit)
            assert(ship.status === false)
        }
      }

      "change status when sunk" in {
        ship.hit(0, 2) match {
          case Success(newShip) =>
            ship = newShip
            assert(ship.shipCoordinates(0) get "value" contains fieldIsHit)
            assert(ship.shipCoordinates(1) get "value" contains fieldIsHit)
            assert(ship.shipCoordinates(2) get "value" contains fieldIsHit)
            assert(ship.status === true)
          case _ => fail("should work, but didnt though")
        }
      }
    }
  }
}
