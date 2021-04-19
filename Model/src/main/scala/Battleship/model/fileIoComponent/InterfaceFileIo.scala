package Battleship.model.fileIoComponent

import Battleship.model.playerComponent.InterfacePlayer

trait InterfaceFileIo {

  def save(player_01: InterfacePlayer, player_02: InterfacePlayer, gameState: String, playerState: String): Unit

  def load(player_01: InterfacePlayer, player_02: InterfacePlayer): (InterfacePlayer, InterfacePlayer, String, String)
}
