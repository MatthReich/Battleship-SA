package Battleship.controller.controllerComponent

object GameState extends Enumeration {

  type GameState = Value
  val PLAYERSETTING, SHIPSETTING, IDLE, SOLVED, SAVED, LOADED = Value

  val map: Map[GameState, String] = Map[GameState, String](
    PLAYERSETTING -> "players are set",
    SHIPSETTING -> "ships are set",
    IDLE -> "",
    SOLVED -> "Game successfully finished",
    SAVED -> "Game Saved",
    LOADED -> "Game Loaded"
  )

  def message(gameStatus: GameState): String = {
    map(gameStatus)
  }

}
