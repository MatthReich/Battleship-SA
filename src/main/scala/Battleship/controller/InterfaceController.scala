package Battleship.controller

import Battleship.controller.controllerComponent.GameState.GameState
import Battleship.controller.controllerComponent.PlayerState.PlayerState
import Battleship.model.playerComponent.InterfacePlayer

import scala.swing.Publisher

trait InterfaceController extends Publisher {

  def player_01: InterfacePlayer

  def player_02: InterfacePlayer

  def gameState: GameState

  def playerState: PlayerState

  def setShip(input: String): Unit

  def setGuess(input: String): Unit

  def setName(input: String): Unit

  def changeGameState(gameState: GameState): Unit

  def changePlayerState(playerState: PlayerState): Unit

}



