package Battleship.controller.controllerbaseimpl

import Battleship.controller.InterfaceController
import Battleship.controller.controllerbaseimpl.GameState.{GameState, PLAYERSETTING}
import Battleship.controller.controllerbaseimpl.PlayerState.{PLAYER_ONE, PLAYER_TWO, PlayerState}
import Battleship.model.playerComponent.InterfacePlayer
import Battleship.model.shipComponent.shipImplemenation.Ship
import scala.collection.mutable


import scala.swing.Publisher

case class Controller(var player_01: InterfacePlayer, var player_02: InterfacePlayer, gameState: GameState, playerState: PlayerState) extends InterfaceController with Publisher {

  val shipNotSunk = false

  override def setShip(input: String): Unit = {
    val coords: Array[mutable.Map[String, Int]] = calculateCoords()

    val functionHelper = changePlayerStats(coords) _
    playerState match {
      case PLAYER_ONE => player_01 = functionHelper(player_01)
      case PLAYER_TWO => player_02 = functionHelper(player_02)
    }
  }

  override def setGuess(input: String): Unit = {

  }

  override def setName(input: String): Unit = {
    playerState match {
      case PLAYER_ONE => player_01 = player_01.updateName(input)
      case PLAYER_TWO => player_02 = player_02.updateName(input)
    }
  }

  def init(): Controller = {
    this.copy(gameState = PLAYERSETTING, playerState = PLAYER_ONE)
  }

  private def changePlayerStats(coords: Array[mutable.Map[String, Int]])(player: InterfacePlayer): InterfacePlayer = {
    val retVal = player.grid.setField(gameState, coords)
    val updatedGrid = retVal._1
    val success = retVal._2

    if (success) {
      val ship = Ship(getSize(coords), coords, shipNotSunk)
      player.updateShip(ship).updateGrid(updatedGrid)
    } else {
      player
    }
  }

  private def calculateCoords(): Array[mutable.Map[String, Int]] = {
    Array(mutable.Map("x" -> 0))
  }

  private def getSize(coords: Array[mutable.Map[String, Int]]): Int = coords.length

}
