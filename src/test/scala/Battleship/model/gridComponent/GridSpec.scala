package Battleship.model.gridComponent

import Battleship.model.gridComponent.gridImplementation.Grid
import Battleship.model.shipComponent.shipImplemenation.Ship
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.mutable.ListBuffer

class GridSpec extends AnyWordSpec {

  val size = 10
  val listOfShips = new ListBuffer[Ship]

  "A Grid" when {

    val grid = new Grid(size, listOfShips)

    "new" should {
      "have no ships" in {
        assert(grid.listOfShips.isEmpty)
      }
      "say that game is not finished" in {
        assert(!grid.winStatement())
      }
    }

    "ship get setted" should {
      "change grid values" in {

      }
      "not allow a ship with coordinates already exists" in {

      }
    }

    "a ship gets hit" should {
      "change grid values" in {

      }
    }

    "game is finished" should {
      "say game is finished" in {

      }
    }
  }

}
