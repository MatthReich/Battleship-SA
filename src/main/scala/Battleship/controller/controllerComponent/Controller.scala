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

import scala.annotation.tailrec
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
            else Failure(new Exception(getMessage(value, convertedInput.length)))
          case Right(value) =>
            if (convertedInput.length == value) calculateCoords(convertedInput.toVector)
            else Failure(new Exception(getMessage(value, convertedInput.length)))
        }
      case Failure(_) => Failure(new Exception("failed to convert input into ints"))
    }
  }

  private def calculateCoords(convertedInput: Vector[Int]): Try[Vector[Map[String, Int]]] = {
    if (checkShipFormat(convertedInput)) calculateCoordsMappingRec(convertedInput(0), convertedInput(2), convertedInput(1), convertedInput(3), Vector[Map[String, Int]]())
    else Failure(new Exception("coords are not in a line"))
  }

  private def checkShipFormat(splitInput: Vector[Int]): Boolean = {
    !((splitInput(0) == splitInput(2) && splitInput(1) == splitInput(3))
      || (!(splitInput(0) == splitInput(2)) && !(splitInput(1) == splitInput(3))))
  }

  @tailrec
  private def calculateCoordsMappingRec(startX: Int, endX: Int, startY: Int, endY: Int, result: Vector[Map[String, Int]]): Try[Vector[Map[String, Int]]] = {
    if (startX > endX && startY == endY || startX == endX && startY > endY) Success(result)
    else if (startX == endX) calculateCoordsMappingRec(startX, endX, startY + 1, endY, result.appended(Map("x" -> startX, "y" -> startY, "value" -> 1))) // 3 4 3 6 -> y hoch zählen
    else if (startY == endY) calculateCoordsMappingRec(startX + 1, endX, startY, endY, result.appended(Map("x" -> startX, "y" -> startY, "value" -> 1))) // 3 4 5 4 -> x hochzählen
    else Failure(new Exception("cannot calculate coords"))
  }

  private def getMessage(expected: Int, got: Int): String = {
    "wrong amount of arguments: expected " + expected + " but got " + got + "!"
  }

}
