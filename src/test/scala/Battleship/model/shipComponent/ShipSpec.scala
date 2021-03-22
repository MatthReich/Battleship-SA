package Battleship.model.shipComponent

import Battleship.model.shipComponent.shipImplemenation.Ship
import org.scalatest.wordspec.AnyWordSpec

class ShipSpec extends AnyWordSpec {

  val shipLength = 3
  val shipArray = Array(
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
        assert(ship.shipCoordinates === shipArray)
      }
      "have a status which is false" in {
        assert(ship.status === false)
      }
    }

    "get hit" should {
      "change value of hit field" in {
        ship.hit(0, 0)
        assert(ship.shipCoordinates(0).get("value") === Some(0))
      }
      "change status when sunk" in {
        ship.hit(0, 1)
        ship.hit(0, 2)
        assert(ship.status === true)
      }
    }
  }
}
