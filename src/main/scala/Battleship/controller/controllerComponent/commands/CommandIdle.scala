package Battleship.controller.controllerComponent.commands

import Battleship.controller.controllerComponent._
import Battleship.controller.controllerComponent.events.{GameWon, PlayerChanged, RedoTurn}
import Battleship.controller.controllerComponent.states.{GameState, PlayerState}
import Battleship.model.playerComponent.InterfacePlayer
import Battleship.utils.Command

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class CommandIdle(input: String, controller: Controller, coordsCalculation: (Int, String) => Option[Array[mutable.Map[String, Int]]]) extends Command {
  override def doStep(): Unit = {
    setGuess()
  }

  private def setGuess(): Unit = {
    val retVal = coordsCalculation(2, input)
    retVal match {
      case Some(coords) =>
        val x = coords(0).getOrElse("x", Int.MaxValue)
        val y = coords(0).getOrElse("y", Int.MaxValue)

        val functionHelper = handleGuess(x, y) _
        controller.playerState match {
          case PlayerState.PLAYER_ONE =>
            controller.player_02 = functionHelper(controller.player_02)
            if (checkWinStatement(controller.player_02)) {
              controller.changeGameState(GameState.SOLVED)
              controller.publish(new GameWon)
            } else {
              controller.changePlayerState(PlayerState.PLAYER_TWO)
              controller.publish(new PlayerChanged)
            }
          case PlayerState.PLAYER_TWO =>
            controller.player_01 = functionHelper(controller.player_01)
            if (checkWinStatement(controller.player_01)) {
              controller.changeGameState(GameState.SOLVED)
              controller.publish(new GameWon)
            } else {
              controller.changePlayerState(PlayerState.PLAYER_ONE)
              controller.publish(new PlayerChanged)
            }
        }
      case None => controller.publish(new RedoTurn)
    }
  }

  private def handleGuess(x: Int, y: Int)(player: InterfacePlayer): InterfacePlayer = {
    val newPlayer = player.updateGrid(player.grid.setField(controller.gameState, Array(mutable.Map("x" -> x, "y" -> y)))._1)

    val indexes = new ListBuffer[Int]
    player.shipList.foreach(ship => indexes.addOne(ship.shipCoordinates.indexWhere(mapping => mapping.get("x").contains(x) &&
      mapping.get("y").contains(y))))
    for (i <- indexes.indices) {
      if (indexes(i) != -1) {
        val index = player.shipList.filter(ship => ship.shipCoordinates.length > indexes(i)).indexWhere(mapping => mapping.shipCoordinates(indexes(i)).get("x").contains(x) && mapping.shipCoordinates(indexes(i)).get("y").contains(y))
        return newPlayer.updateShip(index, player.shipList(index).hit(x, y))
      }
    }
    newPlayer
  }

  private def checkWinStatement(player: InterfacePlayer): Boolean = {
    !player.shipList.exists(_.status == false)
  }

  override def undoStep(): Unit = {

  }

}
