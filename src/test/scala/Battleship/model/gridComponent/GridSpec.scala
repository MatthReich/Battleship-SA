package Battleship.model.gridComponent

import Battleship.model.gridComponent.gridImplementation.Grid
import Battleship.model.gridComponent.strategyCollide.StrategyCollideNormal
import Battleship.model.shipComponent.shipImplemenation.Ship
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.mutable.ListBuffer

class GridSpec extends AnyWordSpec {

  val size = 10
  val listOfShips = new ListBuffer[Ship]
  val strategyCollide: InterfaceStrategyCollide = new StrategyCollideNormal
  val shipArray: Array[scala.collection.mutable.Map[String, Int]] = Array(
    scala.collection.mutable.Map("x" -> 0, "y" -> 0, "value" -> 1),
    scala.collection.mutable.Map("x" -> 0, "y" -> 1, "value" -> 1),
    scala.collection.mutable.Map("x" -> 0, "y" -> 2, "value" -> 1)
  )
  val status = false
  val shipLength = 2


  "A Grid" when {

    val grid = new Grid(size, listOfShips, strategyCollide)

    "new" should {
      "have no ships" in {
        assert(grid.listOfShips.isEmpty)
      }
      "say that game is not finished" in {
        assert(grid.winStatement() === false)
      }
      "have the right dimension" in {
        assert(grid.size === size)
      }
    }

    "ship get set" should {
      "change grid values" in {

      }
      "not work with coordinates already exists" in {

      }
    }

    "a ship gets hit" should {
      "change grid values" in {

      }
    }

    "get a ship" should {
      val askedShip: Ship = new Ship(shipLength, shipArray, status)

      "return the right ship" in {

        assert(grid.getShip(0, 0) === askedShip)
      }
    }

    "game is finished" should {
      "say game is finished" in {
        assert(grid.winStatement() === true)
      }
    }
  }

}
