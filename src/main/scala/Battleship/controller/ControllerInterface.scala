package Battleship.controller

import Battleship.controller.controllerComponent.states.GameStates
import Battleship.controller.controllerComponent.states.PlayerStates
import scala.swing.Publisher
import Battleship.model.playerComponent.PlayerInterface


trait ControllerInterface extends Publisher:
    def player_01: PlayerInterface

    def player_02: PlayerInterface

    def gameState: GameStates

    def playerState: PlayerStates

    def updatePlayer(oldPlayer: String, newPlayer: PlayerInterface): Unit

    def changeGameState(gameState: GameStates): Unit

    def changePlayerState(playerState: PlayerStates): Unit

    def doTurn(input: String): Unit

    def redoTurn(): Unit

    def save(): Unit

    def load(): Unit