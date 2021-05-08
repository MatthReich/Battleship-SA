package Battleship.controller

import Battleship.controller.controllerComponent.states.GameState.GameState
import Battleship.controller.controllerComponent.states.PlayerState.PlayerState

trait InterfaceController {

  def gameState: GameState

  def playerState: PlayerState

  def changeGameState(gameState: GameState): Unit

  def changePlayerState(playerState: PlayerState): Unit

  def doTurn(input: String): Unit

  def redoTurn(): Unit

  def save(): Unit

  def load(): Unit

  def requestNewReaction(string: String, string2: String): Unit

  def requestGameIsWon(player: String): Boolean

  def requestShipSettingFinished(player: String): Boolean

  def requestChangePlayerName(player: String, newName: String): Option[Throwable]

  def requestHandleFieldSettingShipSetting(player: String, coords: Vector[Map[String, Int]]): Option[Throwable]

  def requestHandleFieldSettingIdle(player: String, coords: Vector[Map[String, Int]]): Either[Boolean, Throwable]

}



