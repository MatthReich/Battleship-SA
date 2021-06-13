package Battleship.controller.controllerComponent

import Battleship.controller.ControllerInterface
import Battleship.controller.controllerComponent.states.PlayerStates
import Battleship.controller.controllerComponent.states.GameStates
import scala.swing.Publisher
import com.google.inject.{Guice, Injector}
import Battleship.GameModul
import Battleship.model.playerComponent.PlayerInterface
import Battleship.model.playerComponent.playerImplementation.Player

case class Controller(
    var player_01: PlayerInterface = Player(),
    var player_02: PlayerInterface = Player(),
    var gameState: GameStates = GameStates.PLAYERSETTING,
    var playerState: PlayerStates = PlayerStates.PLAYER_ONE)
    extends ControllerInterface, Publisher:

    override def updatePlayer(oldPlayer: String, newPlayer: PlayerInterface) =
        oldPlayer match
            case "player_01" => this.player_01 = newPlayer
            case "player_02" => this.player_02 = newPlayer

    override def changeGameState(gameState: GameStates): Unit                = this.gameState = gameState

    override def changePlayerState(playerState: PlayerStates): Unit = this.playerState = playerState

    def doTurn(input: String): Unit = ???
    
    def load(): Unit = ???
    
    def redoTurn(): Unit = ???
    
    def save(): Unit = ???
