package Battleship

import Battleship.aview.gui.StartGui
import Battleship.aview.tui.Tui
import Battleship.controller.InterfaceController
import Battleship.controller.controllerComponent._
import Battleship.controller.controllerComponent.events.{GameStart, NewGameGui, PlayerChanged}
import Battleship.controller.controllerComponent.states.{GameState, PlayerState}
import Battleship.model.gridComponent.gridImplementation.Grid
import Battleship.model.gridComponent.strategyCollide.StrategyCollideNormal
import Battleship.model.playerComponent.playerImplementation.Player
import Battleship.model.shipComponent.InterfaceShip

import scala.swing.Reactor

object Game extends Reactor {
  var controller: InterfaceController = initController()
  var tui = new Tui(controller)
  var gui = new StartGui(controller)

  listenTo(controller)


  def main(args: Array[String]): Unit = {
    var input: String = ""

    controller.publish(new GameStart)
    controller.publish(new PlayerChanged)
    listenTo(controller)
    reactions += {
      case _: NewGameGui => initNewGame()
    }
    do {

      input = scala.io.StdIn.readLine()
      if (input == "q") System.exit(0)
      else if (input == "n") initNewGame()
      else if (input == "s") controller.save()
      else if (input == "l") controller.load()
      else if (input == "r") controller.redoTurn()
      else controller.doTurn(input)

    } while (true)
  }

  private def initNewGame(): Unit = {
    controller = initController()
    tui = new Tui(controller)
    gui = new StartGui(controller)
    controller.publish(new GameStart)
    controller.publish(new PlayerChanged)
  }

  private def initController(): InterfaceController = {
    new Controller(
      Player("player_01", Map(2 -> 2, 3 -> 1, 4 -> 0), Vector[InterfaceShip](), Grid(10, new StrategyCollideNormal, Vector[Map[String, Int]]()).initGrid()),
      Player("player_02", Map(2 -> 2, 3 -> 1, 4 -> 0), Vector[InterfaceShip](), Grid(10, new StrategyCollideNormal, Vector[Map[String, Int]]()).initGrid()),
      GameState.PLAYERSETTING, PlayerState.PLAYER_ONE)
  }

}