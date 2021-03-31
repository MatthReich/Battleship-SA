package Battleship.aview.tui

import Battleship.controller.InterfaceController
import Battleship.controller.controllerComponent.events._
import Battleship.controller.controllerComponent.states.{GameState, PlayerState}

import scala.swing.Reactor

class Tui(controller: InterfaceController) extends Reactor {
  listenTo(controller)
  reactions += {
    case _: GameStart =>
      println("Yeah you play the best game in the world... probably :)")
    case _: PlayerChanged =>
      controller.gameState match {
        case GameState.PLAYERSETTING =>
          printTui("set your Name")
        case GameState.SHIPSETTING =>
          printTui("set your Ship <x y x y>\n" + gridAsString() + "\n" + "left:\n" + shipSetListAsString())
        case GameState.IDLE =>
          printTui("guess the enemy ship <x y>\n\n" + "enemy\n" + enemyGridAsString() + "you\n" + gridAsString())
        case GameState.SOLVED =>
          printTui("has won ::: should not print")
      }
    case _: GridUpdated =>
      controller.gameState match {
        case GameState.SHIPSETTING =>
          printTui("set your Ship <x y x y>\n" + gridAsString() + "\n" + "left:\n" + shipSetListAsString())
      }
    case _: RedoTurn =>
      controller.gameState match {
        case GameState.PLAYERSETTING =>
          printTui("holy shit ist das game falsch gelaufen")
        case GameState.SHIPSETTING =>
          printTui("try again .. set your Ship <x y x y>\n" + gridAsString() + "left:\n" + shipSetListAsString())
        case GameState.IDLE =>
          printTui("try again <x y>\n\n" + "enemy\n" + enemyGridAsString() + "you\n" + gridAsString())
      }
    case _: GameWon =>
      printTui("has won")
      println("<n> for new game <q> for end")
  }

  private def printTui(string: String): Unit = {
    controller.playerState match {
      case PlayerState.PLAYER_ONE => println(Console.MAGENTA + controller.player_01.name + Console.RESET + " " + string)
      case PlayerState.PLAYER_TWO => println(Console.CYAN + controller.player_02.name + Console.RESET + " " + string)
    }
  }

  private def gridAsString(): String = {
    controller.playerState match {
      case PlayerState.PLAYER_ONE => controller.player_01.grid.toString(true)
      case PlayerState.PLAYER_TWO => controller.player_02.grid.toString(true)
    }
  }

  private def enemyGridAsString(): String = {
    controller.playerState match {
      case PlayerState.PLAYER_ONE => controller.player_02.grid.toString(false)
      case PlayerState.PLAYER_TWO => controller.player_01.grid.toString(false)
    }
  }

  private def shipSetListAsString(): String = {
    val field = new StringBuilder()
    controller.playerState match {
      case PlayerState.PLAYER_ONE =>
        controller.player_01.shipSetList.foreach(field.append(_).append("\n"))
        field.toString()
      case PlayerState.PLAYER_TWO =>
        controller.player_02.shipSetList.foreach(field.append(_).append("\n"))
        field.toString()
    }
  }

}
