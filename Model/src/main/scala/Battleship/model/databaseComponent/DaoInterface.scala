package Battleship.model.databaseComponent

import Battleship.model.playerComponent.InterfacePlayer

trait DaoInterface {
  def load(): (String, String)

  def save(gameState: String, playerState: String): Unit
}
