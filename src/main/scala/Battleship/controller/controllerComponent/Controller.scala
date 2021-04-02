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
import scala.util.{Failure, Success, Try}

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
      case GameState.SHIPSETTING => undoManager.doStep(new CommandShipSetting(input, this, handleInput))
      case GameState.IDLE => undoManager.doStep(new CommandIdle(input, this, handleInput))
    }
  }

  private def handleInput(input: String, state: Either[Int, Int]): Try[Vector[Map[String, Int]]] = {
    Try(input.split(" ").map(_.toInt)) match {
      case Success(convertedInput) =>
        if (convertedInput.exists(_.>=(player_01.grid.size))) return Failure(new Exception("input is out of scope"))
        state match {
          case Left(value) =>
            if (convertedInput.length == value) Success(Vector(Map("x" -> convertedInput(0), "y" -> convertedInput(1), "value" -> 0)))
            else Failure(new Exception("wrong amount of arguments: was " + convertedInput.length + " but expected " + value + "!"))
          case Right(value) =>
            if (convertedInput.length == value) calculateCoords(convertedInput.toVector)
            else Failure(new Exception("wrong amount of arguments: was " + convertedInput.length + " but expected " + value + "!"))

        }
      case Failure(_) => Failure(new Exception("failed to convert input into ints"))
    }
  }

  private def calculateCoords(splitInput: Vector[Int]): Try[Vector[Map[String, Int]]] = {
    if (checkShipFormat(splitInput)) Success(calculateCoordsMapping(splitInput))
    else Failure(new Exception("coords are not in a line"))
  }

  private def checkShipFormat(splitInput: Vector[Int]): Boolean = {
    !((splitInput(0) == splitInput(2) && splitInput(1) == splitInput(3))
      || (!(splitInput(0) == splitInput(2)) && !(splitInput(1) == splitInput(3))))
  }

  private def calculateCoordsMapping(convertedInput: Vector[Int]): Vector[Map[String, Int]] = {
    val coordsMapping = new Array[mutable.Map[String, Int]](getShipSize(convertedInput, calcDiff))
    var i = 0
    for (x <- convertedInput(0) to convertedInput(2)) {
      for (y <- convertedInput(1) to convertedInput(3)) {
        coordsMapping(i) = mutable.Map("x" -> x, "y" -> y, "value" -> 1)
        i += 1
      }
    }
    coordsMapping.map(_.toMap).toVector
  }

  private def getShipSize(coordsShip: Vector[Int], calcDiff: (Int, Int) => Int): Int = {
    if (coordsShip(0) == coordsShip(2)) {
      math.max(calcDiff(coordsShip(1), coordsShip(3)), calcDiff(coordsShip(3), coordsShip(1)))
    } else {
      math.max(calcDiff(coordsShip(0), coordsShip(2)), calcDiff(coordsShip(2), coordsShip(0)))
    }
  }

  private def calcDiff(nr1: Int, nr2: Int): Int = nr1 - nr2 + 1

}
