package Battleship.controller.controllerComponent

import Battleship.controller.InterfaceController
import Battleship.controller.controllerComponent.commands.{CommandIdle, CommandPlayerSetting, CommandShipSetting}
import Battleship.controller.controllerComponent.states.GameState
import Battleship.controller.controllerComponent.states.GameState.GameState
import Battleship.controller.controllerComponent.states.PlayerState.PlayerState
import Battleship.model.fileIoComponent.InterfaceFileIo
import Battleship.model.fileIoComponent.fileIoJsonImplementation.FileIo
import Battleship.model.playerComponent.InterfacePlayer
import Battleship.utils.UndoManager
import com.google.inject.Inject

import scala.collection.mutable
import scala.swing.Publisher
import scala.util.Try

class Controller @Inject()(var player_01: InterfacePlayer, var player_02: InterfacePlayer, var gameState: GameState, var playerState: PlayerState) extends InterfaceController with Publisher {
  private val undoManager = new UndoManager
  private val fileIo: InterfaceFileIo = new FileIo()

  override def changeGameState(gameState: GameState): Unit = this.gameState = gameState

  override def changePlayerState(playerState: PlayerState): Unit = this.playerState = playerState

  override def save(): Unit = fileIo.save(player_01, player_02, gameState, playerState)

  override def load(): Unit = fileIo.load(this)

  override def redoTurn(): Unit = undoManager.undoStep()

  override def doTurn(input: String): Unit = {
    gameState match {
      case GameState.PLAYERSETTING => undoManager.doStep(new CommandPlayerSetting(input, this))
      case GameState.SHIPSETTING => undoManager.doStep(new CommandShipSetting(input, this, coordsCalculation))
      case GameState.IDLE => undoManager.doStep(new CommandIdle(input, this, coordsCalculation))
    }
  }

  private def coordsCalculation(size: Int, input: String): Option[Vector[Map[String, Int]]] = {
    val splitInput = input.split(" ")
    if (splitInput.length == size) {
      if (Try(splitInput.map(_.toInt)).isFailure) return None
      val convertedInput = splitInput.map(_.toInt)
      size match {
        case 2 => return Some(Array(mutable.Map("x" -> convertedInput(0), "y" -> convertedInput(1), "value" -> 0).toMap).toVector)
        case 4 => if (checkShipFormat(convertedInput)) return Some(calculateCoordsArray(convertedInput).toVector)
        case _ => None
      }
    }
    None
  }

  private def checkShipFormat(splitInput: Array[Int]): Boolean = {
    !((splitInput(0) == splitInput(2) && splitInput(1) == splitInput(3))
      || (!(splitInput(0) == splitInput(2)) && !(splitInput(1) == splitInput(3))))
  }

  private def calculateCoordsArray(convertedInput: Array[Int]): Array[Map[String, Int]] = {
    val coordsArray = new Array[mutable.Map[String, Int]](getShipSize(convertedInput, calcDiff))
    var i = 0
    for (x <- convertedInput(0) to convertedInput(2)) {
      for (y <- convertedInput(1) to convertedInput(3)) {
        coordsArray(i) = mutable.Map("x" -> x, "y" -> y, "value" -> 1)
        i += 1
      }
    }
    coordsArray.map(_.toMap)

  }

  private def getShipSize(coordsShip: Array[Int], calcDiff: (Int, Int) => Int): Int = {
    if (coordsShip(0) == coordsShip(2)) {
      math.max(calcDiff(coordsShip(1), coordsShip(3)), calcDiff(coordsShip(3), coordsShip(1)))
    } else {
      math.max(calcDiff(coordsShip(0), coordsShip(2)), calcDiff(coordsShip(2), coordsShip(0)))
    }
  }

  private def calcDiff(nr1: Int, nr2: Int): Int = nr1 - nr2 + 1

}
