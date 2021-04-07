package Battleship.controller.controllerComponent.commands

import Battleship.controller.controllerComponent.Controller
import Battleship.controller.controllerComponent.events.PlayerChanged
import Battleship.controller.controllerComponent.states.{GameState, PlayerState}
import Battleship.controller.utils.Command

class CommandPlayerSetting(input: String, controller: Controller) extends Command {
  override def doStep(): Unit = {
    controller.playerState match {
      case PlayerState.PLAYER_ONE =>
        if (input != "")
          controller.player_01 = controller.player_01.updateName(input)
        handlePlayerNameSetting(PlayerState.PLAYER_TWO, GameState.PLAYERSETTING)
      case PlayerState.PLAYER_TWO =>
        if (input != "")
          controller.player_02 = controller.player_02.updateName(input)
        handlePlayerNameSetting(PlayerState.PLAYER_ONE, GameState.SHIPSETTING)
    }
  }

  private def handlePlayerNameSetting(newPlayerState: PlayerState.PlayerState, newGameState: GameState.GameState): Unit = {
    controller.changePlayerState(newPlayerState)
    controller.changeGameState(newGameState)
    controller.publish(new PlayerChanged)
  }

  override def undoStep(): Unit = {}

}
