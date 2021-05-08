package Battleship.aview.gui

import Battleship.AkkaHttpGui
import Battleship.aview.gui.panel.FieldPanel
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.{Get, Post}
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import play.api.libs.json.Json

import java.awt.Color
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.swing._

//noinspection HttpUrlsUsage
class Gui() extends Frame {

  val controllerHttp: String = sys.env.getOrElse("CONTROLLERHTTPSERVER", "localhost:8081")
  val modelHttp: String = sys.env.getOrElse("MODELHTTPSERVER", "localhost:8080")
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  listenTo(AkkaHttpGui)

  val dimWidth = 1600
  val dimHeight = 900
  title = "Battleship"
  background = Color.GRAY
  preferredSize = new Dimension(dimWidth, dimHeight)
  val gridSize: Int = 10
  redraw()

  override def closeOperation() {
    System.exit(0)
  }

  reactions += {
    case _: PlayerChanged =>
      requestState("getGameState") match {
        case "SHIPSETTING" => redraw()
        case "IDLE" => redraw()
      }
    case _: GridUpdated =>
      requestState("getGameState") match {
        case "SHIPSETTING" => redraw()
      }
    case _: RedoTurn =>
      requestState("getGameState") match {
        case "SHIPSETTING" =>
        case "IDLE" =>
      }
    case _: TurnAgain => redraw()
    case _: GameWon =>
      redraw()
      newGameOrQuit()
    case _: Saved => saveEventDialog()
    case _: Loaded =>
      redraw()
      loadEventDialog()
    case exception: FailureEvent => redoTurnAlert(exception.getMessage())
    case _ =>
  }

  def evaluateShip(input: String): Unit = {
    if (input == last) {}
    else {
      shipCoords += 1
      ship += input
      last = input
      if (shipCoords == 1) ship += " "
      else {
        requestGameTurn("DOTURN", ship)
        ship = ""
        shipCoords = 0
        redraw()
      }
    }
  }

  def gridPanel(showAllShips: Boolean, grid: Vector[Map[String, Int]], gameState: String): GridPanel = new GridPanel(gridSize + 1, gridSize) {
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
      val fieldPanel = new FieldPanel(showAllShips, column, row, gameState, Gui.this, grid)
      contents += fieldPanel.field
      listenTo(fieldPanel)
    }
  }

  var ship: String = ""
  var last: String = ""
  var shipCoords: Int = 0

  private def textGrid = new GridPanel(1, 2) {
    contents += new TextArea("player_01")
    contents += new TextArea("player_02")
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
    val gameState: String = requestState("getGameState")
    requestState("getPlayerState") match {
      case "PLAYER_ONE" =>
        contents += gridPanel(showAllShips, requestGrid("player_01", showAllShips), gameState)
        contents += gridPanel(showNotAllShips, requestGrid("player_02", showNotAllShips), gameState)
      case "PLAYER_TWO" =>
        contents += gridPanel(showNotAllShips, requestGrid("player_01", showNotAllShips), gameState)
        contents += gridPanel(showAllShips, requestGrid("player_02", showAllShips), gameState)
    }
  }

  private def saveEventDialog(): Unit = {
    Dialog.showMessage(contents.head, "Game has saved!", "Saving State", Dialog.Message.Info)
  }

  private def loadEventDialog(): Unit = {
    Dialog.showMessage(contents.head, "Game has loaded!", "Saving State", Dialog.Message.Info)
  }

  private def redoTurnAlert(cause: String): Unit = {
    Dialog.showMessage(contents.head, cause, "Alert", Dialog.Message.Warning)
  }

  private def newGameOrQuit(): Unit = {
    val retVal = Dialog.showConfirmation(contents.head, "Start new Game?", optionType = Dialog.Options.YesNo, title = title)
    if (retVal == Dialog.Result.No) sys.exit(0)
    else if (retVal == Dialog.Result.Yes) {
      this.visible = false
      requestGameTurn("NEWGAMEVIEW", "")
    }
  }

  menuBar = new MenuBar {
    contents += new Menu("File") {
      contents += new MenuItem(Action("save") {
        requestGameTurn("SAVE", "")
      })
      contents += new MenuItem(Action("load") {
        requestGameTurn("LOAD", "")
      })
      contents += new MenuItem(Action("quit") {
        newGameOrQuit()
      })
    }
    contents += new Menu("Edit") {
      contents += new MenuItem(Action("Undo") {
        requestGameTurn("REDOTURN", "")
      })
    }
  }

  centerOnScreen()

  private def requestGameTurn(event: String, input: String): Unit = {
    val payload = Json.obj(
      "event" -> event.toUpperCase,
      "input" -> input
    )
    Await.result(Http().singleRequest(Post(s"http://$controllerHttp/controller/update", payload.toString())), atMost = 10.second)
  }

  private def requestState(state: String): String = {
    val responseFuture: Future[HttpResponse] = Http().singleRequest(Get(s"http://${controllerHttp}/controller/request?" + state + "=state"))
    val result = Await.result(responseFuture, atMost = 10.second)
    val tmp = Json.parse(Await.result(Unmarshal(result).to[String], atMost = 10.second))
    tmp.result.toOption match {
      case Some(value) => value.as[String]
      case None => ""
    }
  }

  private def requestGrid(player: String, showAll: Boolean): Vector[Map[String, Int]] = {
    val responseFuture: Future[HttpResponse] = Http().singleRequest(Get(s"http://${modelHttp}/model?getPlayerGrid=" + player + showAll))
    val result = Await.result(responseFuture, atMost = 10.second)
    val tmp = Json.parse(Await.result(Unmarshal(result).to[String], atMost = 10.second))
    tmp.result.toOption match {
      case Some(value) => value.as[Vector[Map[String, Int]]]
      case None => Vector[Map[String, Int]]()
    }
  }

}
