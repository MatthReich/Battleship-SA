package Battleship.model.gridComponent

import Battleship.controller.controllerComponent.states.GameState
import Battleship.model.gridComponent.gridImplementation.Grid
import Battleship.model.gridComponent.strategyCollide.StrategyCollideNormal
import org.scalatest.wordspec.AnyWordSpec

import scala.util.{Failure, Success}

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

    "when a three field ship will getting set" should {
      "return indexes of three fields" in {
        strategyNormal.collide(shipArray, grid.grid) match {
          case Left(_) => fail("should represent that there is no collision")
          case Right(indexes) =>
            assert(indexes.length === 3)
            assert(indexes(0) == 0)
            assert(indexes(1) == 10)
            assert(indexes(2) == 20)
        }
      }
    }

    "a Ship is set at a position where is not a ship is set" should {
      "return right " in {
        strategyNormal.collide(shipArray, grid.grid) match {
          case Left(_) => fail("should represent that there is no collision")
          case Right(indexes) =>
            assert(indexes.length === 3)
            assert(indexes(0) == 0)
            assert(indexes(1) == 10)
            assert(indexes(2) == 20)
        }
      }
    }

    "a Ship is set at a position where is already a ship is set" should {
      "return left" in {
        grid.setField(shipSet, Vector(Map("x" -> 0, "y" -> 0), Map("x" -> 0, "y" -> 1))) match {
          case Failure(_) => fail("grid is not working!")
          case Success(newGrid) => grid = newGrid
        }

        strategyNormal.collide(shipArray, grid.grid) match {
          case Left(indexes) =>
            assert(indexes.length === 3)
            assert(indexes(0) == 0)
            assert(indexes(1) == 10)
            assert(indexes(2) == 20)
          case Right(_) => fail("should represent that there is already a ship")
        }
      }
    }
  }
}
