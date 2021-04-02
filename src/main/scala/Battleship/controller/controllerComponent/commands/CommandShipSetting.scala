package Battleship.controller.controllerComponent.commands

import Battleship.controller.controllerComponent._
import Battleship.controller.controllerComponent.events.{GridUpdated, PlayerChanged, RedoTurn}
import Battleship.controller.controllerComponent.states.{GameState, PlayerState}
import Battleship.model.playerComponent.InterfacePlayer
import Battleship.model.shipComponent.shipImplemenation.Ship
import Battleship.utils.Command

import scala.util.{Failure, Success, Try}

class CommandShipSetting(input: String, controller: Controller, coordsCalculation: (String, Either[Int, Int]) => Try[Vector[Map[String, Int]]]) extends Command {

  override def doStep(): Unit = setShip()

  private def setShip(): Unit = {
    val retVal = coordsCalculation(input, Right(4))
    retVal match {
      case Success(coords) =>
        val functionHelper = changePlayerStats(coords) _
        controller.playerState match {
          case PlayerState.PLAYER_ONE =>
            if (shipSettingAllowsNewShip(coords.length, controller.player_01)) {
              val retVal = functionHelper(controller.player_01)
              controller.player_01 = retVal._1
              if (retVal._2) {
                controller.player_01 = controller.player_01.updateShipSetList(coords.length)
                handleShipSetFinishing(controller.player_01, PlayerState.PLAYER_TWO, GameState.SHIPSETTING)
                return
              }
            }
            controller.publish(new RedoTurn)
          case PlayerState.PLAYER_TWO =>
            if (shipSettingAllowsNewShip(coords.length, controller.player_02)) {
              val retVal = functionHelper(controller.player_02)
              controller.player_02 = retVal._1
              if (retVal._2) {
                controller.player_02 = controller.player_02.updateShipSetList(coords.length)
                handleShipSetFinishing(controller.player_02, PlayerState.PLAYER_ONE, GameState.IDLE)
                return
              }
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

  private def changePlayerStats(coords: Vector[Map[String, Int]])(player: InterfacePlayer): (InterfacePlayer, Boolean) = {
    val retVal = player.grid.setField(controller.gameState, coords)
    val updatedGrid = retVal._1
    val success = retVal._2
    if (success) {
      val shipNotSunk = false
      val ship = Ship(coords.length, coords, shipNotSunk)
      (player.addShip(ship).updateGrid(updatedGrid), true)
    } else {
      (player, false)
    }
  }

  override def undoStep(): Unit = {}

}
