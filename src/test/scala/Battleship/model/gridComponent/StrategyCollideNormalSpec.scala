package Battleship.model.gridComponent

import Battleship.controller.controllerComponent.states.GameState
import Battleship.model.gridComponent.gridImplementation.Grid
import Battleship.model.gridComponent.strategyCollide.StrategyCollideNormal
import org.scalatest.wordspec.AnyWordSpec

class StrategyCollideNormalSpec extends AnyWordSpec {

  val size: Int = 10
  val strategyCollide: InterfaceStrategyCollide = new StrategyCollideNormal
  val shipArray: Vector[Map[String, Int]] = Vector(
    Map("x" -> 0, "y" -> 0),
    Map("x" -> 0, "y" -> 1),
    Map("x" -> 0, "y" -> 2)
  )
  val shipSet: GameState.Value = GameState.SHIPSETTING
  var grid: InterfaceGrid = Grid(size, strategyCollide, Vector(Map())).initGrid()

  "A collision" when {

    val strategyNormal: InterfaceStrategyCollide = new StrategyCollideNormal

    "a Ship is set at a position where is not a ship is set" should {
      "return right " in {
        strategyNormal.collide(shipArray, grid.grid) match {
          case Left(_) => fail("should represent that there is no collision")
          case Right(_) => assert(true)
        }
      }
    }

    "a Ship is set at a position where is already a ship is set" should {
      "return left" in {
        println(grid.toString(true))
        grid = grid.setField(shipSet, Vector(Map("x" -> 0, "y" -> 0)))._1
        grid = grid.setField(shipSet, Vector(Map("x" -> 0, "y" -> 1)))._1
        println(grid.toString(true))

        strategyNormal.collide(shipArray, grid.grid) match {
          case Left(_) => assert(true)
          case Right(_) => fail("should represent that there is already a ship")
        }
      }
    }
  }
}
