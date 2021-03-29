package Battleship.controller

import Battleship.controller.controllerbaseimpl.{Controller, GameState, PlayerState}
import Battleship.model.gridComponent.InterfaceGrid
import Battleship.model.gridComponent.gridImplementation.Grid
import Battleship.model.gridComponent.strategyCollide.StrategyCollideNormal
import Battleship.model.playerComponent.InterfacePlayer
import Battleship.model.playerComponent.playerImplementation.Player
import Battleship.model.shipComponent.InterfaceShip
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class ControllerSpec extends AnyWordSpec {

  val gridPlayer_01: InterfaceGrid = Grid(10, new StrategyCollideNormal, new Array[mutable.Map[String, Int]](0)).initGrid()
  val player_01: InterfacePlayer = Player("", new ListBuffer[InterfaceShip], gridPlayer_01)

  val gridPlayer_02: InterfaceGrid = Grid(10, new StrategyCollideNormal, new Array[mutable.Map[String, Int]](0)).initGrid()
  val player_02: InterfacePlayer = Player("", new ListBuffer[InterfaceShip], gridPlayer_02)

  "A Controller" when {

    var controller: InterfaceController = new Controller(player_01, player_02, GameState.PLAYERSETTING, PlayerState.PLAYER_ONE)

    "new" should {
      "init with right parameters" in {
        assert(controller.gameState === GameState.PLAYERSETTING)
        assert(controller.playerState === PlayerState.PLAYER_ONE)
      }
    }
    "while game" should {
      "set name" in {
        controller.setName("player01")
        assert(controller.player_01.name === "player01")
      }
      "set ship" in {
        controller = new Controller(player_01, player_02, GameState.SHIPSETTING, PlayerState.PLAYER_ONE)
        controller.setShip("0 0 0 3")
        assert(controller.player_01.shipList.head.shipLength === 4)
        assert(controller.player_01.grid.grid(0).getOrElse("value", Int.MaxValue) === 1)
        assert(controller.player_01.grid.grid(10).getOrElse("value", Int.MaxValue) === 1)
        assert(controller.player_01.grid.grid(20).getOrElse("value", Int.MaxValue) === 1)
        assert(controller.player_01.grid.grid(30).getOrElse("value", Int.MaxValue) === 1)
      }
    }
    "set Guess" should {
      "change grid" in {
        controller.setShip("0 0 0 3")
        controller.changeGameState(GameState.IDLE)
        controller.setGuess("0 0")
        assert(player_01.shipList(0).shipCoordinates(0).getOrElse("value", Int.MaxValue) === 0)
        assert(player_01.shipList(0).shipCoordinates(1).getOrElse("value", Int.MaxValue) === 1)
        assert(player_01.grid.grid(0).getOrElse("value", Int.MaxValue) === 3)
      }
    }
  }

}
