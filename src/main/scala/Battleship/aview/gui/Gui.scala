package Battleship.aview.gui

import java.awt.Color

import Battleship.controller.InterfaceController
import Battleship.controller.controllerComponent.PlayerState.{PLAYER_ONE, PLAYER_TWO}
import Battleship.controller.controllerComponent.{GameState, GridUpdated, PlayerChanged}

import scala.swing._

class Gui(controller: InterfaceController) extends Frame {
  listenTo(controller)
  val dimWidth = 1600
  val dimHeight = 900
  title = "Battleship"
  background = Color.GRAY
  preferredSize = new Dimension(dimWidth, dimHeight) // maybe fullscreen setting / 1600 * 900 / 800 * 600
  redraw
  reactions += {
    case changed: PlayerChanged =>
      controller.gameState match {
        case GameState.SHIPSETTING =>
        case GameState.IDLE =>
      }
    case updated: GridUpdated =>
  }

  def redraw: Unit = {
    contents = new BorderPanel {
      add(textGrid, BorderPanel.Position.North)
      add(playGrid, BorderPanel.Position.Center)
    }
  }

  private def textGrid = new GridPanel(1, 2) {
    contents += new TextArea(controller.player_01.name)
    contents += new TextArea(controller.player_02.name)
  }

  private def playGrid = new GridPanel(1, 2) {
    controller.playerState match {
      case PLAYER_ONE =>
        contents += new TextArea(controller.player_01.grid.toString(true))
        contents += new TextArea(controller.player_02.grid.toString(false))
      case PLAYER_TWO =>
        contents += new TextArea(controller.player_01.grid.toString(false))
        contents += new TextArea(controller.player_02.grid.toString(true))
    }
  }
}
