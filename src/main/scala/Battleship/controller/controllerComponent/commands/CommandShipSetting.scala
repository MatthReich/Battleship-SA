package Battleship.controller.controllerComponent.commands

import Battleship.controller.controllerComponent._
import Battleship.controller.controllerComponent.events.{GridUpdated, PlayerChanged, RedoTurn}
import Battleship.controller.controllerComponent.states.{GameState, PlayerState}
import Battleship.model.playerComponent.InterfacePlayer
import Battleship.model.shipComponent.shipImplemenation.Ship
import Battleship.utils.Command

import scala.util.{Failure, Success, Try}

class CommandShipSetting(input: String, controller: Controller, coordsCalculation: (String, Either[Int, Int]) => Try[Vector[Map[String, Int]]]) extends Command {

  val shipNotSunk = false

  override def doStep(): Unit = setShip()

  private def setShip(): Unit = {
    val retVal = coordsCalculation(input, Right(4))
    retVal match {
      case Success(coords) =>
        val functionHelper = changePlayerStats(coords) _
        controller.playerState match {
          case PlayerState.PLAYER_ONE =>
            functionHelper(controller.player_01) match {
              case Left(value) =>
                controller.player_01 = value
                controller.player_01 = controller.player_01.updateShipSetList(coords.length)
                handleShipSetFinishing(controller.player_01, PlayerState.PLAYER_TWO, GameState.SHIPSETTING)
                return
              case Right(exception) => println(exception.getMessage)
            }
            controller.publish(new RedoTurn)
          case PlayerState.PLAYER_TWO =>
            functionHelper(controller.player_02) match {
              case Left(value) =>
                controller.player_02 = value
                controller.player_02 = controller.player_02.updateShipSetList(coords.length)
                handleShipSetFinishing(controller.player_02, PlayerState.PLAYER_ONE, GameState.IDLE)
                return
              case Right(exception) => println(exception.getMessage)
            }
            controller.publish(new RedoTurn)
        }
      case Failure(exception) =>
        println(exception.getMessage)
        controller.publish(new RedoTurn)
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
    player.shipSetList.getOrElse(2, Int.MaxValue) == 0 && player.shipSetList.getOrElse(3, Int.MaxValue) == 0 && player.shipSetList.getOrElse(4, Int.MaxValue) == 0
  }

  private def shipSettingAllowsNewShip(coordsLength: Int, player: InterfacePlayer): Boolean = {
    player.shipSetList.getOrElse(coordsLength, Int.MinValue) > 0
  }

  private def changePlayerStats(coords: Vector[Map[String, Int]])(player: InterfacePlayer): Either[InterfacePlayer, Throwable] = {
    if (!shipSettingAllowsNewShip(coords.length, player)) return Right(new Exception("no more ships of this length can be placed"))
    player.grid.setField(controller.gameState, coords) match {
      case Failure(exception) => Right(exception)
      case Success(updatedGrid) =>
        val ship = Ship(coords.length, coords, shipNotSunk)
        Left(player.addShip(ship).updateGrid(updatedGrid))
    }
  }

  override def undoStep(): Unit = {}

}
