package Battleship.controller.controllerComponent

import Battleship.controller.InterfaceController
import Battleship.controller.controllerComponent.GameState.GameState
import Battleship.controller.controllerComponent.PlayerState.PlayerState
import Battleship.controller.controllerComponent.commands.{CommandIdle, CommandPlayerSetting, CommandShipsetting}
import Battleship.model.playerComponent.InterfacePlayer
import Battleship.utils.UndoManager
import com.google.inject.Inject

import scala.collection.mutable
import scala.swing.Publisher
import scala.util.Try

class Controller @Inject()(var player_01: InterfacePlayer, var player_02: InterfacePlayer, var gameState: GameState, var playerState: PlayerState) extends InterfaceController with Publisher {
  private val undoManager = new UndoManager

  override def changeGameState(gameState: GameState): Unit = {
    this.gameState = gameState
  }

  override def changePlayerState(playerState: PlayerState): Unit = {
    this.playerState = playerState
  }

  override def doTurn(input: String): Unit = {
    gameState match {
      case GameState.PLAYERSETTING => undoManager.doStep(new CommandPlayerSetting(input, this))
      case GameState.SHIPSETTING => undoManager.doStep(new CommandShipsetting(input, this))
      case GameState.IDLE => undoManager.doStep(new CommandIdle(input, this))
    }

  }

  def redoTurn(): Unit = {
    undoManager.undoStep()
  }

  def calculateCoords(size: Int)(input: String): Option[Array[mutable.Map[String, Int]]] = {
    val splitInput = input.split(" ")
    if (splitInput.length == size) {
      if (Try(splitInput.map(_.toInt)).isFailure) return None
      val convertedInput = splitInput.map(_.toInt)
      size match {
        case 2 => return Some(Array(mutable.Map("x" -> convertedInput(0), "y" -> convertedInput(1), "value" -> 0)))
        case 4 => if (checkShipFormat(convertedInput)) return Some(calculateCoordsArray(convertedInput))
        case _ => None
      }
    }
    None
  }

  private def checkShipFormat(splitInput: Array[Int]): Boolean = {
    !((splitInput(0) == splitInput(2) && splitInput(1) == splitInput(3))
      || (!(splitInput(0) == splitInput(2)) && !(splitInput(1) == splitInput(3))))
  }

  private def calculateCoordsArray(convertedInput: Array[Int]): Array[mutable.Map[String, Int]] = {
    val coordsArray = new Array[mutable.Map[String, Int]](getShipSize(convertedInput))
    var i = 0
    for (x <- convertedInput(0) to convertedInput(2)) {
      for (y <- convertedInput(1) to convertedInput(3)) {
        coordsArray(i) = mutable.Map("x" -> x, "y" -> y, "value" -> 1)
        i += 1
      }
    }
    coordsArray
  }

  private def getShipSize(coordsShip: Array[Int]): Int = {
    if (coordsShip(0) == coordsShip(2)) {
      math.max(calcDiff(coordsShip(1), coordsShip(3)), calcDiff(coordsShip(3), coordsShip(1)))
    } else {
      math.max(calcDiff(coordsShip(0), coordsShip(2)), calcDiff(coordsShip(2), coordsShip(0)))
    }
  }

  private def calcDiff(nr1: Int, nr2: Int): Int = {
    nr1 - nr2 + 1
  }
}
