package Battleship.aview.gui

import Battleship.aview.gui.panel.FieldPanel
import Battleship.controller.InterfaceController
import Battleship.controller.controllerComponent.PlayerState.{PLAYER_ONE, PLAYER_TWO}
import Battleship.controller.controllerComponent._
import Battleship.model.playerComponent.InterfacePlayer

import java.awt.Color
import scala.swing._

class Gui(controller: InterfaceController) extends Frame {
  listenTo(controller)
  val dimWidth = 1600
  val dimHeight = 900
  title = "Battleship"
  background = Color.GRAY
  preferredSize = new Dimension(dimWidth, dimHeight)
  val gridSize = controller.player_01.grid.size
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

  var ship: String = ""
  var last: String = ""
  var shipCoords: Int = 0

  def gridPanel(showAllShips: Boolean, player: InterfacePlayer): GridPanel = new GridPanel(gridSize + 1, gridSize) {
    border = Swing.LineBorder(java.awt.Color.BLACK, 1)
    var idy = 0
    while (idy < gridSize) {
      var idx = 0
      while (idx < gridSize) {
        val fieldPanel = new FieldPanel(showAllShips, idx, idy, controller, Gui.this, player)
        contents += fieldPanel.field
        listenTo(fieldPanel)
        idx += 1
      }
      idy += 1
    }
  }

  def evaluateShip(input: String): Unit = {
    println(input, ship, last)
    if (input == last) {
    }
    else {
      shipCoords += 1
      ship += input
      last = input
      if (shipCoords == 1) ship += " "
      else {
        controller.doTurn(ship)
        ship = ""
        shipCoords = 0
        redraw
      }
    }
  }

  private def playGrid = new GridPanel(1, 2) {
    controller.playerState match {
      case PLAYER_ONE =>
        contents += gridPanel(true, controller.player_01)
        contents += gridPanel(false, controller.player_02)
      case PLAYER_TWO =>
        contents += gridPanel(false, controller.player_01)
        contents += gridPanel(true, controller.player_02)
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

  menuBar = new MenuBar {
    contents += new Menu("File") {
      contents += new MenuItem(Action("save") {

      })
      contents += new MenuItem(Action("load") {

      })
      contents += new MenuItem(Action("quit") {
        newGameOrQuit()
      })
    }
    contents += new Menu("Edit") {
      contents += new MenuItem(Action("Undo") {
      })
    }
  }

  centerOnScreen()
}
