package Battleship.controller

import Battleship.controller.controllerComponent.Controller
import Battleship.controller.controllerComponent.states.{GameState, PlayerState}
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

  val shipSet = Map(0 -> 0)
  val gridPlayer_01: InterfaceGrid = Grid(10, new StrategyCollideNormal, new Array[mutable.Map[String, Int]](0)).initGrid()
  val player_01: InterfacePlayer = Player("", shipSet, new ListBuffer[InterfaceShip], gridPlayer_01)

  val gridPlayer_02: InterfaceGrid = Grid(10, new StrategyCollideNormal, new Array[mutable.Map[String, Int]](0)).initGrid()
  val player_02: InterfacePlayer = Player("", shipSet, new ListBuffer[InterfaceShip], gridPlayer_02)

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

      }
      "set ship with same y coordinates" in {

      }
      "set ship with false coordinates" in {
      }
    }

    "set Guess" should {
      "change grid" in {
      }
    }
  }

}
