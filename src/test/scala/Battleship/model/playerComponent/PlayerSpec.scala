package Battleship.model.playerComponent

import Battleship.controller.controllerComponent.GameState
import Battleship.model.gridComponent.InterfaceGrid
import Battleship.model.gridComponent.gridImplementation.Grid
import Battleship.model.gridComponent.strategyCollide.StrategyCollideNormal
import Battleship.model.playerComponent.playerImplementation.Player
import Battleship.model.shipComponent.InterfaceShip
import Battleship.model.shipComponent.shipImplemenation.Ship
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class PlayerSpec extends AnyWordSpec {

  val name: String = "Matthias"
  val newName: String = "Marcel"
  val shipList = new ListBuffer[InterfaceShip]
  val grid: InterfaceGrid = Grid(10, new StrategyCollideNormal, new Array[mutable.Map[String, Int]](0)).initGrid()
  val shipLength = 3
  val shipArray: Array[scala.collection.mutable.Map[String, Int]] = Array(
    scala.collection.mutable.Map("x" -> 0, "y" -> 0, "value" -> 1),
    scala.collection.mutable.Map("x" -> 0, "y" -> 1, "value" -> 1),
    scala.collection.mutable.Map("x" -> 0, "y" -> 2, "value" -> 1)
  )
  val shipArrayForUpdate: Array[scala.collection.mutable.Map[String, Int]] = Array(
    scala.collection.mutable.Map("x" -> 0, "y" -> 0, "value" -> 0),
    scala.collection.mutable.Map("x" -> 0, "y" -> 1, "value" -> 1),
    scala.collection.mutable.Map("x" -> 0, "y" -> 2, "value" -> 1)
  )
  val status = false
  val ship: InterfaceShip = Ship(shipLength, shipArray, status)
  val updatedShip: InterfaceShip = Ship(shipLength, shipArrayForUpdate, status)


  "A Player" when {

    val shipSet = Map("" -> 0)
    var player: InterfacePlayer = Player(name, shipSet, shipList, grid)

    "new" should {
      "have a name" in {
        assert(player.name === name)
      }
      "have an empty List" in {
        assert(player.shipList.isEmpty === true)
      }
      "have an grid with no ships in it" in {
        assert(player.grid.grid.exists(_.get("value").contains(1)) === false)
      }
    }
    "name gets updated" should {
      "change name" in {
        player = player.updateName(newName)
        assert(player.name === newName)
      }
    }
    "grid gets updated" should {
      "update grid" in {
        val newGrid = grid.setField(GameState.SHIPSETTING, Array(mutable.Map("x" -> 0, "y" -> 0)))
        assert(newGrid._2 === true)
        player = player.updateGrid(newGrid._1)
        assert(player.grid.grid(0).getOrElse("value", Int.MaxValue) === 1)
      }
    }
    "ship gets added" should {
      "extend List of ships" in {
        player = player.addShip(ship)
        assert(player.shipList.nonEmpty === true)
        assert(player.shipList.length === 1)
      }
    }
    "ship gets updated" should {
      "change value of a ship" in {
        assert(player.shipList.head.shipCoordinates(0).getOrElse("value", Int.MaxValue) === 1)
        player = player.updateShip(0, updatedShip)
        assert(player.shipList.head.shipCoordinates(0).getOrElse("value", Int.MaxValue) === 0)
      }
    }
  }

}
