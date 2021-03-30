package Battleship.aview.gui

import Battleship.aview.gui.panel.ImagePanel
import Battleship.controller.InterfaceController
import Battleship.controller.controllerComponent.{GameStart, GameState, PlayerChanged}

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JTextField
import scala.swing._
import scala.swing.event.ButtonClicked

class StartGui(controller: InterfaceController) extends MainFrame {
  listenTo(controller)
  val dimWidth = 1600
  val dimHeight = 900
  title = "Battleship"
  background = Color.GRAY
  preferredSize = new Dimension(dimWidth, dimHeight)

  reactions += {
    case _: GameStart =>
      this.visible = true
    case _: PlayerChanged =>
      if (this.visible && controller.gameState == GameState.SHIPSETTING) {
        this.visible = false
        new Gui(controller).visible = true
      }
  }

  val backgroundIMG: BufferedImage =
    ImageIO.read(new File("src/main/scala/Battleship/aview/gui/media/BattleShipPicture.png"))

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

    val buttons: List[Button] = List(ButtonStartGame)
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
      controller.doTurn(player_one.getText())
      controller.doTurn(player_two.getText())
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

}
