package Battleship.aview.gui

import Battleship.aview.gui.panel.FieldPanel
import Battleship.controller.InterfaceController
import Battleship.controller.controllerComponent.events.{GameWon, GridUpdated, PlayerChanged, RedoTurn}
import Battleship.controller.controllerComponent.states.GameState
import Battleship.controller.controllerComponent.states.PlayerState.{PLAYER_ONE, PLAYER_TWO}
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
  val gridSize: Int = controller.player_01.grid.size
  redraw()

  reactions += {
    case _: PlayerChanged =>
      controller.gameState match {
        case GameState.SHIPSETTING => redraw()
        case GameState.IDLE => redraw()
      }
    case _: GridUpdated =>
      controller.gameState match {
        case GameState.SHIPSETTING => redraw()
      }
    case _: RedoTurn =>
      controller.gameState match {
        case GameState.SHIPSETTING => redoTurnAlert()
        case GameState.IDLE => redoTurnAlert()
      }
    case _: GameWon => newGameOrQuit()
  }

  def evaluateShip(input: String): Unit = {
    if (input == last) {}
    else {
      shipCoords += 1
      ship += input
      last = input
      if (shipCoords == 1) ship += " "
      else {
        controller.doTurn(ship)
        ship = ""
        shipCoords = 0
        redraw()
      }
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
    for {
      row <- 0 until gridSize
    } {
      if (row == 0) {
        contents += new Label("")
      }
      contents += new Label("" + row)
    }
    for {
      row <- 0 until gridSize
      column <- 0 until gridSize
    } {
      if (column == 0) {
        contents += new Label("" + row)

      }
      val fieldPanel = new FieldPanel(showAllShips, column, row, controller, Gui.this, player)
      contents += fieldPanel.field
      listenTo(fieldPanel)
    }
  }

  private def redraw(): Unit = {
    contents = new BorderPanel {
      add(textGrid, BorderPanel.Position.North)
      add(playGrid, BorderPanel.Position.Center)
    }
  }

  private def playGrid: GridPanel = new GridPanel(1, 2) {
    val showAllShips = true
    val showNotAllShips = false

    controller.playerState match {
      case PLAYER_ONE =>
        contents += gridPanel(showAllShips, controller.player_01)
        contents += gridPanel(showNotAllShips, controller.player_02)
      case PLAYER_TWO =>
        contents += gridPanel(showNotAllShips, controller.player_01)
        contents += gridPanel(showAllShips, controller.player_02)
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