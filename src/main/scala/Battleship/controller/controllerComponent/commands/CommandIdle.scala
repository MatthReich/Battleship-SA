package Battleship.controller.controllerComponent.commands

import Battleship.controller.controllerComponent._
import Battleship.controller.controllerComponent.events.{GameWon, PlayerChanged, RedoTurn, TurnAgain}
import Battleship.controller.controllerComponent.states.{GameState, PlayerState}
import Battleship.model.playerComponent.InterfacePlayer
import Battleship.utils.Command

import scala.util.{Failure, Success, Try}

class CommandIdle(input: String, controller: Controller, coordsCalculation: (String, Either[Int, Int]) => Try[Vector[Map[String, Int]]]) extends Command {

  override def doStep(): Unit = setGuess()

  private def setGuess(): Unit = {
    val retVal = coordsCalculation(input, Left(2))
    retVal match {
      case Success(coords) =>
        val x = coords(0).getOrElse("x", Int.MaxValue)
        val y = coords(0).getOrElse("y", Int.MaxValue)

        val functionHelper = handleGuess(x, y) _
        controller.playerState match {
          case PlayerState.PLAYER_ONE =>
            functionHelper(controller.player_02) match {
              case Left(newPlayer) =>
                controller.player_02 = newPlayer
                if (checkWinStatement(controller.player_02)) {
                  controller.changeGameState(GameState.SOLVED)
                  controller.publish(new GameWon)
                } else {
                  controller.publish(new TurnAgain)
                }
              case Right(newPlayer) =>
                controller.player_02 = newPlayer
                controller.changePlayerState(PlayerState.PLAYER_TWO)
                controller.publish(new PlayerChanged)
            }
          case PlayerState.PLAYER_TWO =>
            functionHelper(controller.player_01) match {
              case Left(newPlayer) =>
                controller.player_01 = newPlayer
                if (checkWinStatement(controller.player_01)) {
                  controller.changeGameState(GameState.SOLVED)
                  controller.publish(new GameWon)
                } else {
                  controller.publish(new TurnAgain)
                }
              case Right(newPlayer) =>
                controller.player_01 = newPlayer
                controller.changePlayerState(PlayerState.PLAYER_ONE)
                controller.publish(new PlayerChanged)
            }
        }
      case Failure(exception) =>
        println(exception.getMessage)
        controller.publish(new RedoTurn)
    }
  }

  private def handleGuess(x: Int, y: Int)(player: InterfacePlayer): Either[InterfacePlayer, InterfacePlayer] = {
    val newPlayer = player.updateGrid(player.grid.setField(controller.gameState, Vector(Map("x" -> x, "y" -> y)))._1)
    for (ship <- newPlayer.shipList) yield ship.hit(x, y) match {
      case Success(newShip) => return Left(newPlayer.updateShip(ship, newShip))
      // @TODO besseres Failure handling, vllt durch neues event publishen
      case Failure(exception) => println(exception.getMessage)
    }
    Right(newPlayer)
  }

  private def checkWinStatement(player: InterfacePlayer): Boolean = !player.shipList.exists(_.status == false)

  override def undoStep(): Unit = {

  }

}
