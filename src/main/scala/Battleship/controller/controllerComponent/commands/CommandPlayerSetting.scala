package Battleship.controller.controllerComponent.commands

import Battleship.controller.controllerComponent.Controller
import Battleship.utils.Command
import Battleship.controller.controllerComponent.states.{GameStates, PlayerStates}
import Battleship.controller.controllerComponent.events.PlayerChanged

class CommandPlayerSetting(input: String, controller: Controller) extends Command:

    override def doStep(): Unit = controller.playerState match
        case PlayerStates.PLAYER_ONE =>
            if input.nonEmpty then controller.player_01 = controller.player_01.updateName(input)
            handlePlayerNameSetting(PlayerStates.PLAYER_TWO, GameStates.PLAYERSETTING)
        case PlayerStates.PLAYER_TWO =>
            if input.nonEmpty then controller.player_02 = controller.player_02.updateName(input)
            handlePlayerNameSetting(PlayerStates.PLAYER_ONE, GameStates.SHIPSETTING)

    private def handlePlayerNameSetting(
        newPlayerState: PlayerStates,
        newGameState: GameStates): Unit =
        controller.changePlayerState(newPlayerState)
        controller.changeGameState(newGameState)
        controller.publish(new PlayerChanged)

    override def undoStep(): Unit = {}
