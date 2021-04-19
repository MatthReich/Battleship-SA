package Battleship.controller.controllerComponent.commands.commandComponents

import Battleship.controller.controllerComponent.Controller
import Battleship.controller.controllerComponent.commands.Command
import Battleship.controller.controllerComponent.states.{GameState, PlayerState}

class CommandPlayerSetting(input: String, controller: Controller) extends Command {
  override def doStep(): Unit = {
    controller.playerState match {
      case PlayerState.PLAYER_ONE =>
        controller.requestChangePlayerName("player_01", input) match {
          case None => handlePlayerNameSetting(PlayerState.PLAYER_TWO, GameState.PLAYERSETTING)
          case Some(exception) => controller.requestNewReaction("FAILUREEVENT", exception.getMessage) // controller.publish(new FailureEvent(exception.getMessage))
        }
      case PlayerState.PLAYER_TWO =>
        controller.requestChangePlayerName("player_02", input) match {
          case None => handlePlayerNameSetting(PlayerState.PLAYER_ONE, GameState.SHIPSETTING)
          case Some(exception) => controller.requestNewReaction("FAILUREEVENT", exception.getMessage) // controller.publish(new FailureEvent(exception.getMessage))
        }
    }
  }

  private def handlePlayerNameSetting(newPlayerState: PlayerState.PlayerState, newGameState: GameState.GameState): Unit = {
    controller.changePlayerState(newPlayerState)
    controller.changeGameState(newGameState)
    controller.requestNewReaction("PLAYERCHANGED", "")
    // controller.publish(new PlayerChanged)
  }

  override def undoStep(): Unit = {}

}
