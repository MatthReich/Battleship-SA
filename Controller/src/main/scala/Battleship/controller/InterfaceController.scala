package Battleship.controller

import Battleship.model.playerComponent.InterfacePlayer
import Battleship.model.states.GameState.GameState
import Battleship.model.states.PlayerState.PlayerState

import scala.swing.Publisher

trait InterfaceController extends Publisher {

  def player_01: InterfacePlayer

  def player_02: InterfacePlayer

  def gameState: GameState

  def playerState: PlayerState

  def changeGameState(gameState: GameState): Unit

  def changePlayerState(playerState: PlayerState): Unit

  def doTurn(input: String): Unit

  def redoTurn(): Unit

  def save(): Unit

  def load(): Unit

  def getPlayer(player:String) : Unit

}



