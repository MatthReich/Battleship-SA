package Battleship.model.shipComponent

import Battleship.model.shipComponent.shipImplemenation.Ship
import org.scalatest.wordspec.AnyWordSpec

class ShipSpec extends AnyWordSpec {

  val shipLength = 3
  val shipArray: Array[scala.collection.mutable.Map[String, Int]] = Array(
    scala.collection.mutable.Map("x" -> 0, "y" -> 0, "value" -> 1),
    scala.collection.mutable.Map("x" -> 0, "y" -> 1, "value" -> 1),
    scala.collection.mutable.Map("x" -> 0, "y" -> 2, "value" -> 1)
  )
  val status = false

  "A Ship" when {

    val ship: Ship = new Ship(shipLength, shipArray, status)

    "new" should {
      "have the correct size" in {
        assert(ship.shipLength === shipLength)
      }

      "have the correct array for the whole length" in {
        assert(ship.shipCoordinates(0) get "value" contains 1)
        assert(ship.shipCoordinates(1) get "value" contains 1)
        assert(ship.shipCoordinates(2) get "value" contains 1)
      }

      "have a status which is false" in {
        assert(ship.status === false)
      }
    }

    "get hit" should {
      "change value of hit field" in {
        assert(ship.hit(0, 0) === true)

        assert(ship.shipCoordinates(0) get "value" contains 0)
        assert(ship.shipCoordinates(1) get "value" contains 1)
        assert(ship.shipCoordinates(2) get "value" contains 1)

        assert(ship.status === false)
      }

      "not sunk when a second hit but three can be taken" in {
        assert(ship.hit(0, 1) === true)

        assert(ship.shipCoordinates(0) get "value" contains 0)
        assert(ship.shipCoordinates(1) get "value" contains 0)
        assert(ship.shipCoordinates(2) get "value" contains 1)

        assert(ship.status === false)
      }

      "return false when wrong coordinates are given and not change any value" in {
        assert(ship.hit(7, 7) === false)

        assert(ship.shipCoordinates(0) get "value" contains 0)
        assert(ship.shipCoordinates(1) get "value" contains 0)
        assert(ship.shipCoordinates(2) get "value" contains 1)

        assert(ship.status === false)
      }

      "change status when sunk" in {
        assert(ship.hit(0, 2) === true)

        assert(ship.shipCoordinates(0) get "value" contains 0)
        assert(ship.shipCoordinates(1) get "value" contains 0)
        assert(ship.shipCoordinates(2) get "value" contains 0)

        assert(ship.status === true)
      }
    }
  }
}
