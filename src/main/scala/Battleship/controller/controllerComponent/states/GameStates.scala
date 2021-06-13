package Battleship.controller.controllerComponent.states

enum GameStates(msg: String):
    def getInfo = msg

    case PLAYERSETTING extends GameStates("State: PLAYERSETTING. The players are forced to set their ingame Names")
    case SHIPSETTING extends GameStates("State: SHIPSETTING. The players are forced to set their ships")
    case IDLE extends GameStates("State: IDLE. The players are guessing where the other one placed ships")
    case SOLVED extends GameStates("State: SOLVED. One player won the game")
    case SAVED extends GameStates("State: SAVED. The game gets saved")
    case LOADED extends GameStates("State: LOADED. The game gets loaded to the last save")
end GameStates
