package Battleship.controller.controllerComponent.commands.commandComponents

import Battleship.controller.controllerComponent._
import Battleship.controller.controllerComponent.commands.Command
import Battleship.controller.controllerComponent.events.{FailureEvent, GridUpdated, PlayerChanged, RedoTurn}
import Battleship.model.playerComponent.InterfacePlayer
import Battleship.model.shipComponent.shipImplemenation.Ship
import Battleship.model.states.{GameState, PlayerState}

import scala.util.{Failure, Success, Try}

class CommandShipSetting(input: String, controller: Controller, coordsCalculation: (String, Either[Int, Int]) => Try[Vector[Map[String, Int]]]) extends Command {

  val shipNotSunk = false

  override def doStep(): Unit = {
    coordsCalculation(input, Right(4)) match {
      case Success(coords) =>
        val functionHelper = changePlayerStats(coords) _
        controller.playerState match {
          case PlayerState.PLAYER_ONE =>
            handleFieldSetting(functionHelper(controller.player_01), PlayerState.PLAYER_ONE, coords.length) match {
              case Failure(exception) => publishFailure(exception.getMessage)
              case Success(newGameState) => handleShipSetFinishing(controller.player_01, PlayerState.PLAYER_TWO, newGameState)
            }
          case PlayerState.PLAYER_TWO =>
            handleFieldSetting(functionHelper(controller.player_02), PlayerState.PLAYER_TWO, coords.length) match {
              case Failure(exception) => publishFailure(exception.getMessage)
              case Success(newGameState) => handleShipSetFinishing(controller.player_02, PlayerState.PLAYER_ONE, newGameState)
            }
        }
      case Failure(exception) => publishFailure(exception.getMessage)
    }
  }

  private def publishFailure(cause: String): Unit = {
    controller.publish(new FailureEvent(cause))
    controller.publish(new RedoTurn)
  }

  private def changePlayerStats(coords: Vector[Map[String, Int]])(player: InterfacePlayer): Either[InterfacePlayer, Throwable] = {
    if (!shipSettingAllowsNewShip(coords.length, player)) return Right(new Exception("no more ships of this length can be placed"))
    // outsource :: set field -- get back failure or success
    controller.requestSetField("player_01", coords) match {
      case Success(value) => Right(new Exception(value))
      case Failure(exception) => Right(exception)
    }
    // player.grid.setField(controller.gameState, coords) match {
    //   case Left(_) => Right(new Exception("there is already a ship placed"))
    //   case Right(value) => value match {
    //     case Failure(exception) => Right(exception)
    //     case Success(updatedGrid) => val ship = Ship(coords.length, coords, shipNotSunk)
    //       Left(player.addShip(ship).updateGrid(updatedGrid))
    //   }
    // }
  }

  private def handleFieldSetting(way: Either[InterfacePlayer, Throwable], state: PlayerState.Value, shipLength: Int): Try[GameState.GameState] = {
    way match {
      case Left(value) =>
        if (state == PlayerState.PLAYER_ONE) {
          controller.player_01 = value
          controller.player_01 = controller.player_01.updateShipSetList(shipLength)
          Success(GameState.SHIPSETTING)
        } else {
          controller.player_02 = value
          controller.player_02 = controller.player_02.updateShipSetList(shipLength)
          Success(GameState.IDLE)
        }
      case Right(exception) => Failure(exception)
    }
  }

  private def handleShipSetFinishing(player: InterfacePlayer, newPlayerState: PlayerState.PlayerState, newGameState: GameState.GameState): Unit = {
    if (shipSettingFinished(player)) {
      controller.changeGameState(newGameState)
      controller.changePlayerState(newPlayerState)
      controller.publish(new PlayerChanged)
    } else {
      controller.publish(new GridUpdated)
    }
  }

  private def shipSettingFinished(player: InterfacePlayer): Boolean = {
    !player.shipSetList.exists(_._2 != 0)
  }

  private def shipSettingAllowsNewShip(coordsLength: Int, player: InterfacePlayer): Boolean = {
    player.shipSetList.getOrElse(coordsLength, Int.MinValue) > 0
  }

  override def undoStep(): Unit = {}

}
