package Battleship.model.fileIoComponent

import Battleship.controller.controllerComponent.Controller
import Battleship.controller.controllerComponent.states.GameState.GameState
import Battleship.controller.controllerComponent.states.PlayerState.PlayerState
import Battleship.model.playerComponent.InterfacePlayer

trait InterfaceFileIo {

  def save(player_01: InterfacePlayer, player_02: InterfacePlayer, gameState: GameState, playerState: PlayerState): Unit

  def load(controller: Controller): Unit

}
