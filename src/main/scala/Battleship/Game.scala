package Battleship

import Battleship.aview.tui.Tui
import Battleship.controller.InterfaceController
import Battleship.controller.controllerComponent.{Controller, GameStart, GameState, PlayerChanged, PlayerState}
import Battleship.model.gridComponent.gridImplementation.Grid
import Battleship.model.gridComponent.strategyCollide.StrategyCollideNormal
import Battleship.model.playerComponent.InterfacePlayer
import Battleship.model.playerComponent.playerImplementation.Player
import Battleship.model.shipComponent.InterfaceShip

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.swing.Reactor

object Game extends Reactor {

  var dummyGrid_01: Grid = Grid(10, new StrategyCollideNormal, new Array[mutable.Map[String, Int]](1))
  var dummyGrid_02: Grid = Grid(10, new StrategyCollideNormal, new Array[mutable.Map[String, Int]](2))
  var dummyPlayer_01: InterfacePlayer = Player("player_01", new ListBuffer[InterfaceShip], dummyGrid_01.initGrid())
  var dummyPlayer_02: InterfacePlayer = Player("player_02", new ListBuffer[InterfaceShip], dummyGrid_02.initGrid())

  var controller: InterfaceController = new Controller(dummyPlayer_01, dummyPlayer_02, GameState.PLAYERSETTING, PlayerState.PLAYER_ONE)
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

  def initNewGame(): Unit = {
    dummyGrid_01 = Grid(10, new StrategyCollideNormal, new Array[mutable.Map[String, Int]](3))
    dummyGrid_02 = Grid(10, new StrategyCollideNormal, new Array[mutable.Map[String, Int]](4))
    dummyPlayer_01 = Player("player_01", new ListBuffer[InterfaceShip], dummyGrid_01.initGrid())
    dummyPlayer_02 = Player("player_02", new ListBuffer[InterfaceShip], dummyGrid_02.initGrid())
    controller = new Controller(dummyPlayer_01, dummyPlayer_02, GameState.PLAYERSETTING, PlayerState.PLAYER_ONE)
    tui = new Tui(controller)
    controller.publish(new GameStart)
    controller.publish(new PlayerChanged)
  }

}