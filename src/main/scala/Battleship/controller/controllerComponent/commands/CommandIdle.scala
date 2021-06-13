package Battleship.controller.controllerComponent.commands

import Battleship.controller.controllerComponent._
import Battleship.controller.controllerComponent.events._
import Battleship.controller.controllerComponent.states.{GameStates, PlayerStates}
import Battleship.model.playerComponent.PlayerInterface
import Battleship.utils.Command

import scala.util.{Failure, Success, Try}

class CommandIdle(
    input: String,
    controller: Controller,
    coordsCalculation: (String, Either[Int, Int]) => Try[Vector[Map[String, Int]]])
    extends Command:

    override def doStep(): Unit = coordsCalculation(input, Left(2)) match
        case Success(coords)    =>
            val x = coords(0).getOrElse("x", Int.MaxValue)
            val y = coords(0).getOrElse("y", Int.MaxValue)

            val functionHelper = handleGuess(x, y) _
            controller.playerState match {
                case PlayerStates.PLAYER_ONE =>
                    handleFieldSetting(functionHelper(controller.player_02), PlayerStates.PLAYER_ONE)
                case PlayerStates.PLAYER_TWO =>
                    handleFieldSetting(functionHelper(controller.player_01), PlayerStates.PLAYER_TWO)
            }
        case Failure(exception) => publishFailure(exception.getMessage)

    private def publishFailure(cause: String): Unit =
        controller.publish(new FailureEvent(cause))
        controller.publish(new RedoTurn)

    private def handleGuess(x: Int, y: Int)(player: PlayerInterface): Try[Either[PlayerInterface, PlayerInterface]] =
        player.grid.setFields(controller.gameState, Vector(Map("x" -> x, "y" -> y))) match {
            case Success(value)     =>
                val newPlayer = player.updateGrid(value)
                for (ship <- newPlayer.shipList) yield ship.hit(x, y) match {
                    case Success(newShip) => return Success(Left(newPlayer.updateShip(ship, newShip)))
                    case _                =>
                }
                Success(Right(newPlayer))
            case Failure(exception) => Failure(exception)
        }

    private def handleFieldSetting(
        tryWay: Try[Either[PlayerInterface, PlayerInterface]],
        state: PlayerStates): Unit = tryWay match
        case Failure(exception) => controller.publish(new FailureEvent(exception.getMessage))
        case Success(way)       => way match
                case Left(newPlayer)  =>
                    if state == PlayerStates.PLAYER_ONE then
                        controller.player_02 = newPlayer
                        handleNewGameSituationAndEndGameIfFinished(controller.player_02)
                    else
                        controller.player_01 = newPlayer
                        handleNewGameSituationAndEndGameIfFinished(controller.player_01)
                case Right(newPlayer) =>
                    if state == PlayerStates.PLAYER_ONE then
                        controller.player_02 = newPlayer
                        controller.changePlayerState(PlayerStates.PLAYER_TWO)
                        controller.publish(new PlayerChanged)
                    else
                        controller.player_01 = newPlayer
                        controller.changePlayerState(PlayerStates.PLAYER_ONE)
                        controller.publish(new PlayerChanged)

    private def handleNewGameSituationAndEndGameIfFinished(player: PlayerInterface): Unit =
        if gameIsWonOf(player) then
            controller.changeGameState(GameStates.SOLVED)
            controller.publish(new GameWon)
        else
            controller.publish(new TurnAgain)

    private def gameIsWonOf(player: PlayerInterface): Boolean = !player.shipList.exists(_.status == false)

    override def undoStep(): Unit = {}
