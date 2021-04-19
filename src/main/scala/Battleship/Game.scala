package Battleship

import Battleship.aview.gui.StartGui
import Battleship.aview.tui.Tui
import Battleship.controller.InterfaceController
import Battleship.controller.controllerComponent.Controller
import Battleship.controller.controllerComponent.events.{GameStart, NewGameView, PlayerChanged}
import Battleship.controller.controllerComponent.states.{GameState, PlayerState}

import scala.swing.Reactor

object Game extends Reactor {
  var controller: InterfaceController = initController()
  var tui = new Tui(controller)
  // var gui = new StartGui(controller)

  listenTo(controller)


  def main(args: Array[String]): Unit = {

    controller.publish(new GameStart)
    controller.publish(new PlayerChanged)

    listenTo(controller)

    reactions += {
      case _: NewGameView => initNewGame()
    }

    do {
      tui.tuiProcessLine(scala.io.StdIn.readLine())
    } while (true)

  }

  private def initNewGame(): Unit = {
    controller = initController()
    tui = new Tui(controller)
    // gui = new StartGui(controller)
    controller.publish(new GameStart)
    controller.publish(new PlayerChanged)
  }

  private def initController(): InterfaceController = {
    new Controller(
      GameState.PLAYERSETTING, PlayerState.PLAYER_ONE)
  }

}