package Battleship.controller.controllerComponent.commands

import Battleship.controller.controllerComponent._
import Battleship.controller.controllerComponent.events._
import Battleship.controller.controllerComponent.states.{GameState, PlayerState}
import Battleship.controller.utils.Command
import Battleship.model.playerComponent.InterfacePlayer

import scala.util.{Failure, Success, Try}

class CommandIdle(input: String, controller: Controller, coordsCalculation: (String, Either[Int, Int]) => Try[Vector[Map[String, Int]]]) extends Command {

  override def doStep(): Unit = {
    coordsCalculation(input, Left(2)) match {
      case Success(coords) =>
        val x = coords(0).getOrElse("x", Int.MaxValue)
        val y = coords(0).getOrElse("y", Int.MaxValue)

        val functionHelper = handleGuess(x, y) _
        controller.playerState match {
          case PlayerState.PLAYER_ONE =>
            handleFieldSetting(functionHelper(controller.player_02), PlayerState.PLAYER_ONE)
          case PlayerState.PLAYER_TWO =>
            handleFieldSetting(functionHelper(controller.player_01), PlayerState.PLAYER_TWO)
        }
      case Failure(exception) => publishFailure(exception.getMessage)
    }
  }

  private def publishFailure(cause: String): Unit = {
    controller.publish(new FailureEvent(cause))
    controller.publish(new RedoTurn)
  }

  private def handleGuess(x: Int, y: Int)(player: InterfacePlayer): Try[Either[InterfacePlayer, InterfacePlayer]] = {
    player.grid.setField(controller.gameState, Vector(Map("x" -> x, "y" -> y))) match {
      case Success(value) =>
        val newPlayer = player.updateGrid(value)
        for (ship <- newPlayer.shipList) yield ship.hit(x, y) match {
          case Success(newShip) => return Success(Left(newPlayer.updateShip(ship, newShip)))
          case _ =>
        }
        Success(Right(newPlayer))
      case Failure(exception) => Failure(exception)
    }
  }

  private def handleFieldSetting(tryWay: Try[Either[InterfacePlayer, InterfacePlayer]], state: PlayerState.Value): Unit = {
    tryWay match {
      case Success(way) => way match {
        case Left(newPlayer) =>
          if (state == PlayerState.PLAYER_ONE) {
            controller.player_02 = newPlayer
            handleNewGameSituationAndEndGameIfFinished(controller.player_02)
          } else {
            controller.player_01 = newPlayer
            handleNewGameSituationAndEndGameIfFinished(controller.player_01)
          }
        case Right(newPlayer) =>
          if (state == PlayerState.PLAYER_ONE) {
            controller.player_02 = newPlayer
            controller.changePlayerState(PlayerState.PLAYER_TWO)
            controller.publish(new PlayerChanged)
          } else {
            controller.player_01 = newPlayer
            controller.changePlayerState(PlayerState.PLAYER_ONE)
            controller.publish(new PlayerChanged)
          }
      }
      case Failure(exception) => controller.publish(new FailureEvent(exception.getMessage))
    }
  }

  private def handleNewGameSituationAndEndGameIfFinished(player: InterfacePlayer): Unit = {
    if (gameIsWonOf(player)) {
      controller.changeGameState(GameState.SOLVED)
      controller.publish(new GameWon)
    } else {
      controller.publish(new TurnAgain)
    }
  }

  private def gameIsWonOf(player: InterfacePlayer): Boolean = !player.shipList.exists(_.status == false)

  override def undoStep(): Unit = {

  }

}
