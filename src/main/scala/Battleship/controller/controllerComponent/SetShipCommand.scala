package Battleship.controller.controllerComponent

import Battleship.controller.controllerComponent.GameState.GameState
import Battleship.controller.controllerComponent.PlayerState.{PLAYER_ONE, PLAYER_TWO, PlayerState}
import Battleship.model.playerComponent.InterfacePlayer
import Battleship.model.shipComponent.shipImplemenation.Ship
import Battleship.utils.Command

import scala.collection.mutable

class SetShipCommand(input: String, playerState: PlayerState, gameState: GameState) extends Command {

  override def doStep(): Unit = {
  }

  override def undoStep(): Unit = {

  }

}
