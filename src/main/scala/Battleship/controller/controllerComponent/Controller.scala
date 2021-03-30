package Battleship.controller.controllerComponent

import Battleship.controller.InterfaceController
import Battleship.controller.controllerComponent.GameState.GameState
import Battleship.controller.controllerComponent.PlayerState.PlayerState
import Battleship.model.playerComponent.InterfacePlayer
import Battleship.utils.UndoManager
import com.google.inject.Inject

import scala.swing.Publisher

class Controller (var player_01: InterfacePlayer, var player_02: InterfacePlayer, var gameState: GameState, var playerState: PlayerState) extends InterfaceController with Publisher {

  /*
  - Publisher
  - Gui

  - Error Checks
  - FileIO
   */

  private val undoManager = new UndoManager

  override def changeGameState(gameState: GameState): Unit = {
    this.gameState = gameState
  }

  override def changePlayerState(playerState: PlayerState): Unit = {
    this.playerState = playerState
  }

  override def doTurn(input: String): Unit = {
    undoManager.doStep(new DoTurnCommand(input, this))
  }

  def redoTurn(): Unit = {
    undoManager.undoStep()
  }

}
