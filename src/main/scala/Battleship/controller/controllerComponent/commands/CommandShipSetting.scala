package Battleship.controller.controllerComponent.commands

import Battleship.controller.controllerComponent._
import Battleship.controller.controllerComponent.events.{FailureEvent, GridUpdated, PlayerChanged, RedoTurn}
import Battleship.controller.controllerComponent.states.{GameStates, PlayerStates}
import Battleship.model.playerComponent.PlayerInterface
import Battleship.model.shipComponent.shipImplementation.Ship
import Battleship.utils.Command

import scala.util.{Failure, Success, Try}

class CommandShipSetting(
    input: String,
    controller: Controller,
    coordsCalculation: (String, Either[Int, Int]) => Try[Vector[Map[String, Int]]])
    extends Command:

    val shipNotSunk = false

    override def doStep(): Unit = coordsCalculation(input, Right(4)) match
        case Failure(exception) => publishFailure(exception.getMessage)
        case Success(coords)    =>
            val functionHelper = changePlayerStats(coords) _
            controller.playerState match
                case PlayerStates.PLAYER_ONE => handleFieldSetting(
                        functionHelper(controller.player_01),
                        PlayerStates.PLAYER_ONE,
                        coords.length) match
                        case Failure(exception)    => publishFailure(exception.getMessage)
                        case Success(newGameState) =>
                            handleShipSetFinishing(controller.player_01, PlayerStates.PLAYER_TWO, newGameState)
                case PlayerStates.PLAYER_TWO => handleFieldSetting(
                        functionHelper(controller.player_02),
                        PlayerStates.PLAYER_TWO,
                        coords.length) match
                        case Failure(exception)    => publishFailure(exception.getMessage)
                        case Success(newGameState) =>
                            handleShipSetFinishing(controller.player_02, PlayerStates.PLAYER_ONE, newGameState)

    private def publishFailure(cause: String): Unit =
        controller.publish(new FailureEvent(cause))
        controller.publish(new RedoTurn)

    private def changePlayerStats(coords: Vector[Map[String, Int]])(player: PlayerInterface)
        : Either[PlayerInterface, Throwable] =
        if !shipSettingAllowsNewShip(coords.length, player) then
            return Right(new Exception("no more ships of this length can be placed"))
        player.grid.setFields(controller.gameState, coords) match
            case Failure(exception)   => Right(exception)
            case Success(updatedGrid) =>
                val ship = Ship(coords.length, coords, shipNotSunk)
                Left(player.addShip(ship).updateGrid(updatedGrid))

    private def handleFieldSetting(
        way: Either[PlayerInterface, Throwable],
        state: PlayerStates,
        shipLength: Int): Try[GameStates] = way match
        case Left(value)      =>
            if state == PlayerStates.PLAYER_ONE then
                controller.player_01 = value
                controller.player_01 = controller.player_01.updateShipSetList(shipLength.toString)
                Success(GameStates.SHIPSETTING)
            else
                controller.player_02 = value
                controller.player_02 = controller.player_02.updateShipSetList(shipLength.toString)
                Success(GameStates.IDLE)
        case Right(exception) => Failure(exception)

    private def handleShipSetFinishing(
        player: PlayerInterface,
        newPlayerState: PlayerStates,
        newGameState: GameStates): Unit   =
        if shipSettingFinished(player) then
            controller.changeGameState(newGameState)
            controller.changePlayerState(newPlayerState)
            controller.publish(new PlayerChanged)
        else
            controller.publish(new GridUpdated)

    private def shipSettingFinished(player: PlayerInterface): Boolean = !player.shipSetList.exists(_._2 != 0)

    private def shipSettingAllowsNewShip(coordsLength: Int, player: PlayerInterface): Boolean =
        player.shipSetList.getOrElse(coordsLength.toString, Int.MinValue) > 0

    override def undoStep(): Unit = {}
