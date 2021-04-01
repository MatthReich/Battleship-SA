package Battleship.model.gridComponent

import Battleship.controller.controllerComponent.states.GameState
import Battleship.model.gridComponent.gridImplementation.Grid
import Battleship.model.gridComponent.strategyCollide.StrategyCollideNormal
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.mutable

class GridSpec extends AnyWordSpec {

  val size: Int = 10
  val strategyCollide: InterfaceStrategyCollide = new StrategyCollideNormal
  val idle: GameState.Value = GameState.IDLE
  val shipSet: GameState.Value = GameState.SHIPSETTING
  val shipArray: Vector[Map[String, Int]] = Vector(
    Map("x" -> 0, "y" -> 0),
    Map("x" -> 0, "y" -> 1),
    Map("x" -> 0, "y" -> 2)
  )
  val shipArrayFalse: Vector[Map[String, Int]] = Vector(
    Map("x" -> 0, "y" -> 10),
    Map("x" -> 0, "y" -> 1),
    Map("x" -> 0, "y" -> 2)
  )


  "A Grid" when {

    var grid: InterfaceGrid = Grid(size, strategyCollide, null).initGrid()

    "new" should {
      "have a right grid length" in {
        assert(grid.grid.length === size * size)
      }
      "have the right dimension" in {
        assert(grid.grid.exists(_.get("value").contains(1)) === false)
      }
      "has right collide strategy" in {
        assert(grid.strategyCollide === StrategyCollideNormal())
      }
      "has right size" in {
        assert(grid.size === size)
      }
    }

    "ship get set" should {
      "change grid values" in {
        val retVal = grid.setField(shipSet, shipArray)
        grid = retVal._1

        assert(retVal._2 === true)
        assert(grid.grid(0) === mutable.Map("x" -> 0, "y" -> 0, "value" -> 1))
        assert(grid.grid(10) === mutable.Map("x" -> 0, "y" -> 1, "value" -> 1))
        assert(grid.grid(20) === mutable.Map("x" -> 0, "y" -> 2, "value" -> 1))
      }
      "not work with coordinates already exists" in {
        assert(grid.setField(shipSet, shipArray)._2 === false)
      }
      "not work with coordinates doesnt exists" in {
        assert(grid.setField(idle, shipArrayFalse)._2 === false)
      }
    }

    "a ship gets hit" should {
      "change grid values" in {
        val retVal = grid.setField(idle, Vector(Map("x" -> 0, "y" -> 0)))
        grid = retVal._1

        assert(retVal._2 === true)
        assert(grid.grid(0) === mutable.Map("x" -> 0, "y" -> 0, "value" -> 3))
      }
    }

    "water gets hit" should {
      "change value to water" in {
        val retVal = grid.setField(idle, Vector(Map("x" -> 9, "y" -> 9)))
        grid = retVal._1

        assert(retVal._2 === true)
        assert(grid.grid(99) === Map("x" -> 9, "y" -> 9, "value" -> 2))
      }
    }

  }

}
