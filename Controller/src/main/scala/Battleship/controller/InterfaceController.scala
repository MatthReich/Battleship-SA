package Battleship.controller

import Battleship.controller.controllerComponent.states.GameState.GameState
import Battleship.controller.controllerComponent.states.PlayerState.PlayerState

import scala.swing.Publisher

trait InterfaceController extends Publisher {

  def gameState: GameState

  def playerState: PlayerState

  def changeGameState(gameState: GameState): Unit

  def changePlayerState(playerState: PlayerState): Unit

  def doTurn(input: String): Unit

  def redoTurn(): Unit

  def save(): Unit

  def load(): Unit

  def requestNewReaction(string: String, string2: String)

}



