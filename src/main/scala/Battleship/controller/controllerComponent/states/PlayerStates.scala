package Battleship.controller.controllerComponent.states

enum PlayerStates(msg: String):
    def getInfo = msg

    case PLAYER_ONE extends PlayerStates("State: PLAYER_ONE. The first player is on turn")
    case PLAYER_TWO extends PlayerStates("State: PLAYER_TWO. The second player is on turn")
end PlayerStates
