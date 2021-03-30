package Battleship

import Battleship.aview.tui.Tui
import Battleship.controller.InterfaceController
import Battleship.controller.controllerComponent._
import Battleship.model.gridComponent.gridImplementation.Grid
import Battleship.model.gridComponent.strategyCollide.StrategyCollideNormal
import Battleship.model.playerComponent.playerImplementation.Player
import Battleship.model.shipComponent.InterfaceShip

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.swing.Reactor

object Game extends Reactor {

  var controller: InterfaceController = initController()
  var tui = new Tui(controller)

  def main(args: Array[String]): Unit = {
    var input: String = ""
    controller.publish(new GameStart)
    controller.publish(new PlayerChanged)


    do {

      input = scala.io.StdIn.readLine()
      if (input == "q") System.exit(0)
      else if (input == "n") initNewGame()
      else controller.doTurn(input)

    } while (true)
  }

  private def initNewGame(): Unit = {
    controller = initController()
    tui = new Tui(controller)
    controller.publish(new GameStart)
    controller.publish(new PlayerChanged)
  }

  private def initController(): InterfaceController = {
    new Controller(
      Player("player_01", new ListBuffer[InterfaceShip], Grid(10, new StrategyCollideNormal, new Array[mutable.Map[String, Int]](0)).initGrid()),
      Player("player_02", new ListBuffer[InterfaceShip], Grid(10, new StrategyCollideNormal, new Array[mutable.Map[String, Int]](1)).initGrid()),
      GameState.PLAYERSETTING, PlayerState.PLAYER_ONE)
  }

}