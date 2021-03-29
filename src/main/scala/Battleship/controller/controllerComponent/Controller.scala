package Battleship.controller.controllerComponent

import Battleship.controller.InterfaceController
import Battleship.controller.controllerComponent.GameState.GameState
import Battleship.controller.controllerComponent.PlayerState.{PLAYER_ONE, PLAYER_TWO, PlayerState}
import Battleship.model.playerComponent.InterfacePlayer
import Battleship.utils.UndoManager
import com.google.inject.Inject

import scala.swing.Publisher

class Controller @Inject()(var player_01: InterfacePlayer, var player_02: InterfacePlayer, var gameState: GameState, var playerState: PlayerState) extends InterfaceController with Publisher {

  /*
  - Publisher
  - Main
  - Gui

  - Error Checks
  - FileIO
  - Tui
   */

  private val undoManager = new UndoManager

  override def changeGameState(gameState: GameState): Unit = {
    this.gameState = gameState
  }

  override def changePlayerState(playerState: PlayerState): Unit = {
    this.playerState = playerState
  }

  override def setShip(input: String): Unit = {
    doTurn(input)
  }

  override def setGuess(input: String): Unit = {
    doTurn(input)
  }

  def doTurn(input: String): Unit = {
    undoManager.doStep(new DoTurnCommand(input, this))
  }

  def redoTurn(): Unit = {
    undoManager.undoStep()
  }

  override def setName(input: String): Unit = {
    playerState match {
      case PLAYER_ONE => player_01 = player_01.updateName(input)
      case PLAYER_TWO => player_02 = player_02.updateName(input)
    }
  }

}
