package Battleship.controller

import Battleship.controller.controllerbaseimpl.GameState.GameState
import Battleship.controller.controllerbaseimpl.PlayerState.PlayerState
import Battleship.model.playerComponent.InterfacePlayer

trait InterfaceController {

  def player_01: InterfacePlayer

  def player_02: InterfacePlayer

  def gameState: GameState

  def playerState: PlayerState

  def setShip(input: String): Unit

  def setGuess(input: String): Unit

  def setName(input: String): Unit

}
