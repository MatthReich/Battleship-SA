package Battleship.model.gridComponent

import Battleship.controller.controllerbaseimpl.GameState
import Battleship.model.gridComponent.gridImplementation.Grid
import Battleship.model.gridComponent.strategyCollide.StrategyCollideNormal
import Battleship.model.shipComponent.shipImplemenation.Ship
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class GridSpec extends AnyWordSpec {

  val size = 10
  val strategyCollide: InterfaceStrategyCollide = new StrategyCollideNormal
  val idle = GameState.IDLE


  "A Grid" when {

    var grid: InterfaceGrid = Grid(size, strategyCollide, null).initGrid()

    "new" should {
      "have a right grid length" in {
        assert(grid.grid.length === size*size)
      }
      "have the right dimension" in {
        assert(grid.grid(0) === mutable.Map("x" -> 0, "y" -> 0, "value" -> 0))
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
        grid = grid.setField(idle, Array(mutable.Map("x" -> 0, "y" -> 0)))._1
        assert(grid.grid(0) === mutable.Map("x" -> 0, "y" -> 0, "value" -> 2))
      }
    }

    "get a ship" should {

      "return the right ship" in {
      }
    }

    "game is finished" should {
      "say game is finished" in {
      }
    }
  }

}
