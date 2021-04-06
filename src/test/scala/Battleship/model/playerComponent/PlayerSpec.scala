package Battleship.model.playerComponent

import Battleship.controller.controllerComponent.states.GameState
import Battleship.model.gridComponent.InterfaceGrid
import Battleship.model.gridComponent.gridImplementation.Grid
import Battleship.model.gridComponent.strategyCollide.StrategyCollideNormal
import Battleship.model.playerComponent.playerImplementation.Player
import Battleship.model.shipComponent.InterfaceShip
import Battleship.model.shipComponent.shipImplemenation.Ship
import org.scalatest.wordspec.AnyWordSpec

import scala.util.{Failure, Success}

class PlayerSpec extends AnyWordSpec {

  val name: String = "Matthias"
  val newName: String = "Marcel"
  val shipList: Vector[InterfaceShip] = Vector[InterfaceShip]()
  val grid: InterfaceGrid = Grid(10, new StrategyCollideNormal, Vector[Map[String, Int]]()).initGrid()
  val shipLength = 3
  val shipArray: Vector[Map[String, Int]] = Vector(
    Map("x" -> 0, "y" -> 0, "value" -> 1),
    Map("x" -> 0, "y" -> 1, "value" -> 1),
    Map("x" -> 0, "y" -> 2, "value" -> 1)
  )
  val shipArrayForUpdate: Vector[Map[String, Int]] = Vector(
    Map("x" -> 0, "y" -> 0, "value" -> 0),
    Map("x" -> 0, "y" -> 1, "value" -> 1),
    Map("x" -> 0, "y" -> 2, "value" -> 1)
  )
  val status = false
  val ship: InterfaceShip = Ship(shipLength, shipArray, status)
  val updatedShip: InterfaceShip = Ship(shipLength, shipArrayForUpdate, status)


  "A Player" when {

    var player: InterfacePlayer = Player(name, Map(2 -> 2), shipList, grid)

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
        grid.setField(GameState.SHIPSETTING, Vector(Map("x" -> 0, "y" -> 0))) match {
          case Failure(_) => fail("grid not working correctly")
          case Success(newGrid) =>
            player = player.updateGrid(newGrid)
            assert(player.grid.grid(0).getOrElse("value", Int.MaxValue) === 1)
        }
      }
    }
    "ship set list is updated" should {
      "reduce the right ship by one" in {
        player = player.updateShipSetList(2)
        assert(player.shipSetList.get(2).contains(1))
      }
      "replace the list" in {
        player = player.updateShipSetList(Map(2 -> 2, 3 -> 3))
        assert(player.shipSetList.get(2).contains(2))
        assert(player.shipSetList.get(3).contains(3))
      }
      "return old when expected does not exists" in {
        player = player.updateShipSetList(5)
        assert(player.shipSetList.get(2).contains(2))
        assert(player.shipSetList.get(3).contains(3))
      }
    }

    "ship gets added" should {
      "extend List of ships" in {
        player = player.addShip(ship)
        player = player.addShip(ship)
        assert(player.shipList.nonEmpty === true)
        assert(player.shipList.length === 2)
      }
    }
    "ship gets updated" should {
      "change value of a ship" in {
        assert(player.shipList.head.shipCoordinates(0).getOrElse("value", Int.MaxValue) === 1)
        player = player.updateShip(ship, updatedShip)
        assert(player.shipList.head.shipCoordinates(0).getOrElse("value", Int.MaxValue) === 0)
      }
      "replace whole list" in {
        player = player.updateShip(Vector(updatedShip))
        assert(player.shipList.length === 1)
      }
    }
  }

}
