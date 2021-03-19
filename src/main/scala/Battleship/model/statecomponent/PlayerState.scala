package Battleship.model.statecomponent

object PlayerState extends Enumeration {

  type PlayerState = Value
  val PLAYER_ONE, PLAYER_TWO = Value

  val map: Map[PlayerState, String] = Map[PlayerState, String](
    PLAYER_ONE -> "player_01's turn",
    PLAYER_TWO -> "player_02's turn"
  )

  def message(playerStatus: PlayerState): String = {
    map(playerStatus)
  }

}
