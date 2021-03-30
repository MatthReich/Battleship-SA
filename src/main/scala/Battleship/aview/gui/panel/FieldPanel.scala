package Battleship.aview.gui.panel

import Battleship.aview.gui.Gui
import Battleship.controller.InterfaceController
import Battleship.controller.controllerComponent.GameState
import Battleship.model.playerComponent.InterfacePlayer

import java.awt.Color
import scala.swing.event.UIEvent
import scala.swing._

class FieldPanel(showAllShips: Boolean, x: Int, y: Int, controller: InterfaceController, gui: Gui, player: InterfacePlayer) extends FlowPanel {

  val field: BoxPanel = new BoxPanel(Orientation.Vertical) {
    val water: Int = 0
    val ship: Int = 1
    val waterHit: Int = 2
    val shipHit: Int = 3

    val tmp: Any = player.grid.grid(player.grid.grid.indexWhere(mapping => mapping.get("x").contains(x) && mapping.get("y").contains(y))).getOrElse("value", "holy shit ist das verbuggt")
    tmp match {
      case this.water => background = Color.BLUE; contents += new Label("~")
      case this.ship =>
        if (showAllShips) {
          background = Color.GREEN;
          contents += new Label("x")
        }
        else {
          background = Color.BLUE;
          contents += new Label("~")
        }
      case this.shipHit => background = Color.RED; contents += new Label("x")
      case this.waterHit => background = Color.BLUE; contents += new Label("0")
      case _ =>
    }
    border = Swing.LineBorder(java.awt.Color.BLACK, 1)
    listenTo(mouse.clicks)
    listenTo(controller)
    reactions += {
      case event: UIEvent => {
        event.source.background = Color.GRAY
        val string: String = x + " " + y
        controller.gameState match {
          case GameState.SHIPSETTING => gui.evaluateShip(string)
          case GameState.IDLE => controller.doTurn(string)
          case GameState.SOLVED => sys.exit(0)
        }
        repaint
      }
      case _ => repaint()
    }
  }

}
