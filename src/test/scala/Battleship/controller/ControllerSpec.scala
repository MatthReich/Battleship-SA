package Battleship.controller

import Battleship.controller.controllerComponent.{Controller, GameState, PlayerState}
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

  val name_01: String = "Matthias"
  val name_02: String = "Marcel"


  "A Controller" when {

    val controller: InterfaceController = new Controller(player_01, player_02, GameState.PLAYERSETTING, PlayerState.PLAYER_ONE)

    "new" should {
      "init with right parameters" in {
        assert(controller.gameState === GameState.PLAYERSETTING)
        assert(controller.playerState === PlayerState.PLAYER_ONE)
      }
    }
    "set a new name" should {
      "change player one name" in {
        controller.changePlayerState(PlayerState.PLAYER_ONE)
        assert(controller.player_02.name != name_01)
        controller.doTurn(name_01)
        assert(controller.player_01.name === name_01)
      }
      "change player two name" in {
        controller.changePlayerState(PlayerState.PLAYER_TWO)
        assert(controller.player_02.name != name_02)
        controller.doTurn(name_02)
        assert(controller.player_01.name === name_01)
      }
    }

    "set a new ship" should {
      "set ship with same x coordinates" in {
        controller.changePlayerState(PlayerState.PLAYER_ONE)
        controller.changeGameState(GameState.SHIPSETTING)
        controller.doTurn("0 0 0 3")
        assert(controller.player_01.shipList.head.shipLength === 4)
        assert(controller.player_01.grid.grid(0).getOrElse("value", Int.MaxValue) === 1)
        assert(controller.player_01.grid.grid(10).getOrElse("value", Int.MaxValue) === 1)
        assert(controller.player_01.grid.grid(20).getOrElse("value", Int.MaxValue) === 1)
        assert(controller.player_01.grid.grid(30).getOrElse("value", Int.MaxValue) === 1)
      }
      "set ship with same y coordinates" in {
        controller.changePlayerState(PlayerState.PLAYER_TWO)
        controller.doTurn("0 0 3 0")
        assert(controller.player_02.shipList.head.shipLength === 4)
        controller.player_02.grid.grid.foreach(println(_))
        assert(controller.player_02.grid.grid(0).getOrElse("value", Int.MaxValue) === 1)
        assert(controller.player_02.grid.grid(1).getOrElse("value", Int.MaxValue) === 1)
        assert(controller.player_02.grid.grid(2).getOrElse("value", Int.MaxValue) === 1)
        assert(controller.player_02.grid.grid(3).getOrElse("value", Int.MaxValue) === 1)
      }
      "set ship with false coordinates" in {
        // throws null pointer
      }
    }

    "set Guess" should {
      "change grid" in {
        controller.changePlayerState(PlayerState.PLAYER_ONE)
        controller.changeGameState(GameState.IDLE)
        controller.doTurn("0 0")
        assert(player_01.shipList.head.shipCoordinates(0).getOrElse("value", Int.MaxValue) === 0)
        assert(player_01.shipList.head.shipCoordinates(1).getOrElse("value", Int.MaxValue) === 1)
        assert(player_01.grid.grid(0).getOrElse("value", Int.MaxValue) === 3)
      }
    }
  }

}
