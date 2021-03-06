package Battleship.aview.tui

import scala.swing.event.Event

class GameStart extends Event

class GridUpdated extends Event

class PlayerChanged extends Event

class RedoTurn extends Event

class TurnAgain extends Event

class GameWon extends Event

class NewGame extends Event

class NewGameView extends Event

class ExitGame extends Event

class Saved extends Event

class Loaded extends Event

class FailureEvent(message: String) extends Event {
  def getMessage(): String = message
}