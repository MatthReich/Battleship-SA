package Battleship.controller.controllerbaseimpl

import Battleship.controller.InterfaceController
import Battleship.controller.controllerbaseimpl.GameState.{GameState, PLAYERSETTING}
import Battleship.controller.controllerbaseimpl.PlayerState.{PLAYER_ONE, PLAYER_TWO, PlayerState}
import Battleship.model.playerComponent.InterfacePlayer

import scala.swing.Publisher

case class Controller(var player_01: InterfacePlayer, var player_02: InterfacePlayer, gameState: GameState, playerState: PlayerState) extends InterfaceController with Publisher {

  override def setShip(input: String): Unit = {

  }

  override def setGuess(input: String): Unit = {

  }

  override def setName(input: String): Unit = {
    playerState match {
      case PLAYER_ONE => player_01 = player_01.changeName(input)
      case PLAYER_TWO => player_02 = player_02.changeName(input)
    }
  }

  def init(): Controller = {
    this.copy(gameState = PLAYERSETTING, playerState = PLAYER_ONE)
  }
}
