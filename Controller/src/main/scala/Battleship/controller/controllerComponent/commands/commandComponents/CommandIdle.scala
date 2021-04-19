package Battleship.controller.controllerComponent.commands.commandComponents

import Battleship.controller.controllerComponent._
import Battleship.controller.controllerComponent.commands.Command
import Battleship.controller.controllerComponent.events._
import Battleship.controller.controllerComponent.states.{GameState, PlayerState}

import scala.util.{Failure, Success, Try}

class CommandIdle(input: String, controller: Controller, coordsCalculation: (String, Either[Int, Int]) => Try[Vector[Map[String, Int]]]) extends Command {

  override def doStep(): Unit = {
    coordsCalculation(input, Left(2)) match {
      case Success(coords) =>
        val x = coords(0).getOrElse("x", Int.MaxValue)
        val y = coords(0).getOrElse("y", Int.MaxValue)

        controller.playerState match {
          case PlayerState.PLAYER_ONE =>
            controller.requestHandleFieldSettingIdle("player_02", Vector[Map[String, Int]](Map("x" -> x, "y" -> y))) match {
              case Right(exception) => publishFailure(exception.getMessage)
              case Left(way) => if (way) {
                changePlayer()
              } else {
                handleNewGameSituationAndEndGameIfFinished("player_02")
              }
            }
          case PlayerState.PLAYER_TWO =>
            controller.requestHandleFieldSettingIdle("player_01", Vector[Map[String, Int]](Map("x" -> x, "y" -> y))) match {
              case Right(exception) => publishFailure(exception.getMessage)
              case Left(way) => if (way) {
                changePlayer()
              } else {
                handleNewGameSituationAndEndGameIfFinished("player_01")
              }
            }
        }
      case Failure(exception) => publishFailure(exception.getMessage)
    }
  }

  private def publishFailure(cause: String): Unit = {
    controller.publish(new FailureEvent(cause))
    controller.publish(new RedoTurn)
  }

  private def changePlayer(): Unit = {
    if (controller.playerState == PlayerState.PLAYER_ONE) {
      controller.changePlayerState(PlayerState.PLAYER_TWO)
      controller.publish(new PlayerChanged)
    } else {
      controller.changePlayerState(PlayerState.PLAYER_ONE)
      controller.publish(new PlayerChanged)
    }
  }

  private def handleNewGameSituationAndEndGameIfFinished(player: String): Unit = {

    if (controller.requestGameIsWon(player)) {
      controller.changeGameState(GameState.SOLVED)
      controller.publish(new GameWon)
    } else {
      controller.publish(new TurnAgain)
    }
  }

  override def undoStep(): Unit = {

  }

}
