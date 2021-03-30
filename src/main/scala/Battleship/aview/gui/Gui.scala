package Battleship.aview.gui

import Battleship.controller.InterfaceController
import Battleship.controller.controllerComponent.PlayerState.{PLAYER_ONE, PLAYER_TWO}
import Battleship.controller.controllerComponent._

import java.awt.Color
import scala.swing._

class Gui(controller: InterfaceController) extends Frame {
  listenTo(controller)
  val dimWidth = 1600
  val dimHeight = 900
  title = "Battleship"
  background = Color.GRAY
  preferredSize = new Dimension(dimWidth, dimHeight)
  redraw
  reactions += {
    case _: PlayerChanged =>
      controller.gameState match {
        case GameState.SHIPSETTING => redraw
        case GameState.IDLE => redraw
      }
    case _: GridUpdated =>
      controller.gameState match {
        case GameState.SHIPSETTING => redraw
      }
    case _: RedoTurn =>
      controller.gameState match {
        case GameState.SHIPSETTING => redoTurnAlert()
        case GameState.IDLE => redoTurnAlert()
      }
    case _: GameWon => newGameOrQuit()
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

  private def redoTurnAlert() {
    Dialog.showMessage(contents.head, "Your Input could not be used, please try again", "Alert", Dialog.Message.Warning)
  }

  private def newGameOrQuit(): Unit = {
    val retVal = Dialog.showConfirmation(contents.head, "Start new Game?", optionType = Dialog.Options.YesNo, title = title)
    if (retVal == Dialog.Result.No) sys.exit(0)
    else if (retVal == Dialog.Result.Yes) Console.print("n")
  }

  centerOnScreen()
}
