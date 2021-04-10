package Battleship.model.fileIoComponent

import Battleship.controller.controllerComponent.Controller
import Battleship.model.playerComponent.InterfacePlayer
import Battleship.model.states.GameState.GameState
import Battleship.model.states.PlayerState.PlayerState

trait InterfaceFileIo {

  def save(player_01: InterfacePlayer, player_02: InterfacePlayer, gameState: GameState, playerState: PlayerState): Unit

  def load(controller: Controller): Unit

}
