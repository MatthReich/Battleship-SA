package Battleship.controller

import Battleship.controller.controllerComponent.Controller
import Battleship.controller.controllerComponent.states.{GameState, PlayerState}
import org.scalatest.wordspec.AnyWordSpec

class ControllerSpec extends AnyWordSpec {

  val shipSet = Map(0 -> 0)

  val name_01: String = "Matthias"
  val name_02: String = "Marcel"


  "A Controller" when {

    val controller: InterfaceController = new Controller(GameState.PLAYERSETTING, PlayerState.PLAYER_ONE)

    "new" should {
      "init with right parameters" in {

      }
    }
    "set a new name" should {
      "change player one name" in {

      }
      "change player two name" in {

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
