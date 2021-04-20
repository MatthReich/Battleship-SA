package Battleship.controller.controllerComponent.commands.commandComponents

import Battleship.controller.controllerComponent._
import Battleship.controller.controllerComponent.commands.Command
import Battleship.controller.controllerComponent.states.{GameState, PlayerState}

import scala.util.{Failure, Success, Try}

class CommandShipSetting(input: String, controller: Controller, coordsCalculation: (String, Either[Int, Int]) => Try[Vector[Map[String, Int]]]) extends Command {

  val shipNotSunk = false

  override def doStep(): Unit = {
    coordsCalculation(input, Right(4)) match {
      case Success(coords) =>
        controller.playerState match {
          case PlayerState.PLAYER_ONE =>
            controller.requestHandleFieldSettingShipSetting("player_01", coords) match {
              case Some(exception) => publishFailure(exception.getMessage)
              case None => handleShipSetFinishing("player_01", PlayerState.PLAYER_TWO, GameState.SHIPSETTING)
            }
          case PlayerState.PLAYER_TWO =>
            controller.requestHandleFieldSettingShipSetting("player_02", coords) match {
              case Some(exception) => publishFailure(exception.getMessage)
              case None => handleShipSetFinishing("player_02", PlayerState.PLAYER_ONE, GameState.IDLE)
            }
        }
      case Failure(exception) => publishFailure(exception.getMessage)
    }
  }

  private def handleShipSetFinishing(player: String, newPlayerState: PlayerState.PlayerState, newGameState: GameState.GameState): Unit = {
    if (controller.requestShipSettingFinished(player)) {
      controller.changeGameState(newGameState)
      controller.changePlayerState(newPlayerState)
      controller.requestNewReaction("PLAYERCHANGED", "")
      // controller.publish(new PlayerChanged)
    } else {
      controller.requestNewReaction("GRIDUPDATE", "")
      // controller.publish(new GridUpdated)
    }
  }

  private def publishFailure(cause: String): Unit = {
    controller.requestNewReaction("FAILUREEVENT", cause)
    controller.requestNewReaction("REDOTURN", "")
    // controller.publish(new FailureEvent(cause))
    // controller.publish(new RedoTurn)
  }

  override def undoStep(): Unit = {}

}
