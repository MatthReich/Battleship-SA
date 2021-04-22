package Battleship.aview.gui.panel

import Battleship.AkkaHttpGui
import Battleship.aview.gui.Gui
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Post
import play.api.libs.json.Json

import java.awt.Color
import scala.concurrent.ExecutionContextExecutor
import scala.swing._
import scala.swing.event.UIEvent

class FieldPanel(showAllShips: Boolean, x: Int, y: Int, gameState: String, gui: Gui, grid: Vector[Map[String, Int]]) extends FlowPanel {

  val controllerHttp = "controller-api:8081"

  val field: BoxPanel = new BoxPanel(Orientation.Vertical) {
    val water: Int = 0
    val ship: Int = 1
    val waterHit: Int = 2
    val shipHit: Int = 3

    val tmp: Any = grid(grid.indexWhere(mapping => mapping.get("x").contains(x) && mapping.get("y").contains(y))).getOrElse("value", "holy shit ist das verbuggt")
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
      case this.waterHit => background = Color.CYAN; contents += new Label("0")
      case _ =>
    }
    border = Swing.LineBorder(java.awt.Color.BLACK, 1)
    listenTo(mouse.clicks)
    listenTo(AkkaHttpGui)

    reactions += {
      case event: UIEvent => {
        event.source.background = Color.GRAY
        val string: String = x + " " + y
        gameState match {
          case "SHIPSETTING" => gui.evaluateShip(string)
          case "IDLE" => requestGameTurn("DOTURN", string)
          case "SOLVED" => sys.exit(0)
        }
        repaint
      }
      case _ => repaint()
    }
  }

  private def requestGameTurn(event: String, input: String): Unit = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext
    val payload = Json.obj(
      "event" -> event.toUpperCase,
      "input" -> input
    )
    Http().singleRequest(Post(s"http://${controllerHttp}/controller/update", payload.toString()))
  }
}
