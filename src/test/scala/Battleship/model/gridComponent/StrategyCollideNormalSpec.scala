package Battleship.model.gridComponent

import Battleship.controller.controllerbaseimpl.GameState
import Battleship.model.gridComponent.gridImplementation.Grid
import Battleship.model.gridComponent.strategyCollide.StrategyCollideNormal
import org.scalatest.wordspec.AnyWordSpec

class StrategyCollideNormalSpec extends AnyWordSpec {

  val size: Int = 10
  val strategyCollide: InterfaceStrategyCollide = new StrategyCollideNormal
  val shipArray: Array[scala.collection.mutable.Map[String, Int]] = Array(
    scala.collection.mutable.Map("x" -> 0, "y" -> 0),
    scala.collection.mutable.Map("x" -> 0, "y" -> 1),
    scala.collection.mutable.Map("x" -> 0, "y" -> 2)
  )
  val shipSet: GameState.Value = GameState.SHIPSETTING
  var grid: InterfaceGrid = Grid(size, strategyCollide, null).initGrid()

  "A collision" when {

    val strategyNormal: InterfaceStrategyCollide = new StrategyCollideNormal

    "a Ship is set at a position where is not a ship is set" should {
      "return true" in {
        assert(strategyNormal.collide(shipArray, grid.grid)._1 === false)
      }
    }

    "a Ship is set at a position where is already a ship is set" should {
      "return false" in {
        grid.setField(shipSet, shipArray)
        assert(strategyNormal.collide(shipArray, grid.grid)._1 === true)
      }
    }
  }
}
