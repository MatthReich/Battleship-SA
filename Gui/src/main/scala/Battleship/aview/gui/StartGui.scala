package Battleship.aview.gui

import Battleship.AkkaHttpGui
import Battleship.aview.gui.panel.ImagePanel
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.{Get, Post}
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import play.api.libs.json.Json

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JTextField
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.swing._
import scala.swing.event.ButtonClicked

class StartGui() extends MainFrame {
  listenTo(AkkaHttpGui)
  val controllerHttp: String = sys.env.getOrElse("CONTROLLERHTTPSERVER", "localhost:8081")
  val picturePath: String = sys.env.getOrElse("PICTUREPATH", "Gui/src/main/scala/Battleship/aview/gui/media/BattleShipPicture.png")
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext
  val dimWidth = 1600
  val dimHeight = 900
  title = "Battleship"
  background = Color.GRAY
  preferredSize = new Dimension(dimWidth, dimHeight)
  val gameGui = new Gui()
  gameGui.visible = false

  reactions += {
    case _: GameStart =>
      this.visible = true
    case _: PlayerChanged =>
      if (this.visible && requestState("getGameState") == "SHIPSETTING") {
        this.visible = false
        gameGui.visible = true
      }
    case _ =>
      if (this.visible && requestState("getGameState") != "PLAYERSETTING") {
        this.visible = false
        gameGui.visible = true
      } else if (!gameGui.visible && requestState("getGameState") != "PLAYERSETTING") {
        gameGui.visible = true
      }
  }

  val backgroundIMG: BufferedImage =
    ImageIO.read(new File(picturePath))

  val imageLabel: ImagePanel = new ImagePanel {
    imagePath(backgroundIMG)
    preferredSize = new Dimension(dimWidth, dimHeight)
  }

  val startButton: Panel = new FlowPanel {
    val ButtonStartGame = new Button("start game")
    val exitButton = new Button("exit game")

    ButtonStartGame.background = Color.BLACK
    ButtonStartGame.foreground = Color.WHITE

    exitButton.background = Color.BLACK
    exitButton.foreground = Color.WHITE

    contents += ButtonStartGame
    contents += exitButton

    listenTo(ButtonStartGame)
    listenTo(exitButton)

    reactions += {
      case ButtonClicked(b) =>

        if (b == ButtonStartGame) {
          if (chooseStart() == Dialog.Result.Ok) {
          }
        } else if (b == exitButton) {
          sys.exit(0)
        }
    }
  }

  def chooseStart(): Dialog.Result.Value = {
    val player_one = new JTextField
    val player_two = new JTextField
    val message = Array(" player_one:", player_one, " ", " player_two:", player_two)
    val res = Dialog.showConfirmation(contents.head,
      message,
      optionType = Dialog.Options.YesNo,
      title = title)
    if (res == Dialog.Result.Ok) {
      requestGameTurn("DOTURN", player_one.getText())
      requestGameTurn("DOTURN", player_two.getText())
    }
    res
  }

  menuBar = new MenuBar {
    contents += new Menu("Creators") {
      contents += new MenuItem(scala.swing.Action("Matthias") {
      })
      contents += new Separator()
      contents += new MenuItem(scala.swing.Action("Marcel") {
      })
    }
  }

  contents = new BorderPanel {
    iconImage = backgroundIMG
    add(startButton, BorderPanel.Position.South)
    add(imageLabel, BorderPanel.Position.Center)

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
    val responseFuture: Future[HttpResponse] = Http().singleRequest(Get(s"http://$controllerHttp/controller/request?" + state + "=state"))
    val result = Await.result(responseFuture, atMost = 10.second)
    val tmp = Json.parse(Await.result(Unmarshal(result).to[String], atMost = 10.second))
    tmp.result.toOption match {
      case Some(value) => value.as[String]
      case None => ""
    }
  }
}
