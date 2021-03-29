package Battleship.model.gridComponent

import Battleship.controller.controllerbaseimpl.GameState
import Battleship.model.gridComponent.gridImplementation.Grid
import Battleship.model.gridComponent.strategyCollide.StrategyCollideNormal
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.mutable

class GridSpec extends AnyWordSpec {

  val size: Int = 10
  val strategyCollide: InterfaceStrategyCollide = new StrategyCollideNormal
  val idle: GameState.Value = GameState.IDLE
  val shipSet: GameState.Value = GameState.SHIPSETTING
  val shipArray: Array[scala.collection.mutable.Map[String, Int]] = Array(
    scala.collection.mutable.Map("x" -> 0, "y" -> 0),
    scala.collection.mutable.Map("x" -> 0, "y" -> 1),
    scala.collection.mutable.Map("x" -> 0, "y" -> 2)
  )


  "A Grid" when {

    var grid: InterfaceGrid = Grid(size, strategyCollide, null).initGrid()

    "new" should {
      "have a right grid length" in {
        assert(grid.grid.length === size * size)
      }
      "have the right dimension" in {
        assert(grid.grid(0) === mutable.Map("x" -> 0, "y" -> 0, "value" -> 0))
      }
    }

    "ship get set" should {
      "change grid values" in {
        grid = grid.setField(shipSet, shipArray)._1
        assert(grid.grid(0) === mutable.Map("x" -> 0, "y" -> 0, "value" -> 1))
        assert(grid.grid(10) === mutable.Map("x" -> 0, "y" -> 1, "value" -> 1))
        assert(grid.grid(20) === mutable.Map("x" -> 0, "y" -> 2, "value" -> 1))
      }
      "not work with coordinates already exists" in {

      }
    }

    "a ship gets hit" should {
      "change grid values" in {
        grid = grid.setField(idle, Array(mutable.Map("x" -> 0, "y" -> 0)))._1
        assert(grid.grid(0) === mutable.Map("x" -> 0, "y" -> 0, "value" -> 3))
      }
    }

  }

}
