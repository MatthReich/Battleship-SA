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
          case Left(lengthOfArguments) => calculateCoords(lengthOfArguments, convertedInput.toVector)
          case Right(lengthOfArguments) => calculateCoords(lengthOfArguments, convertedInput.toVector)
        }
      case Failure(_) => Failure(new Exception("failed to convert input into ints"))
    }
  }

  private def calculateCoords(expectedLength: Int, convertedInput: Vector[Int]): Try[Vector[Map[String, Int]]] = {
    if (convertedInput.length != expectedLength) Failure(new Exception("wrong amount of arguments: expected " + expectedLength + " but got " + convertedInput.length + "!"))
    else if (expectedLength == 2) Success(Vector(Map("x" -> convertedInput(0), "y" -> convertedInput(1), "value" -> 0)))
    else if (checkShipFormat(convertedInput)) calculateCoordsMapping(convertedInput)
    else Failure(new Exception("coords are not in a line"))
  }

  private def calculateCoordsMapping(convertedInput: Vector[Int]): Try[Vector[Map[String, Int]]] = {
    if (shipIsPlacedLeftToRight(convertedInput) || shipIsPlacedUpToDown(convertedInput)) calculateCoordsMappingRec(convertedInput(0), convertedInput(2), convertedInput(1), convertedInput(3), Vector[Map[String, Int]]())
    else if (shipIsPlacedRightToLeft(convertedInput)) calculateCoordsMappingRec(convertedInput(0), convertedInput(2), convertedInput(3), convertedInput(1), Vector[Map[String, Int]]())
    else if (shipIsPlacedDownToUp(convertedInput)) calculateCoordsMappingRec(convertedInput(2), convertedInput(0), convertedInput(3), convertedInput(1), Vector[Map[String, Int]]())
    else Failure(new Exception("something strange happened to your coords .. maybe try rice"))
  }

  @tailrec
  private def calculateCoordsMappingRec(startX: Int, endX: Int, startY: Int, endY: Int, result: Vector[Map[String, Int]]): Try[Vector[Map[String, Int]]] = {
    if (startX > endX && startY == endY || startX == endX && startY > endY) Success(result)
    else if (startX == endX) calculateCoordsMappingRec(startX, endX, startY + 1, endY, result.appended(Map("x" -> startX, "y" -> startY, "value" -> 1)))
    else if (startY == endY) calculateCoordsMappingRec(startX + 1, endX, startY, endY, result.appended(Map("x" -> startX, "y" -> startY, "value" -> 1)))
    else Failure(new Exception("cannot calculate coords"))
  }

  private def checkShipFormat(convertedInput: Vector[Int]): Boolean = {
    !((convertedInput(0) == convertedInput(2) && convertedInput(1) == convertedInput(3)) || (!(convertedInput(0) == convertedInput(2)) && !(convertedInput(1) == convertedInput(3))))
  }

  private def shipIsPlacedLeftToRight(convertedInput: Vector[Int]): Boolean = {
    convertedInput(0) == convertedInput(2) && convertedInput(1) < convertedInput(3)
  }

  private def shipIsPlacedUpToDown(convertedInput: Vector[Int]): Boolean = {
    convertedInput(0) < convertedInput(2) && convertedInput(1) == convertedInput(3)
  }

  private def shipIsPlacedRightToLeft(convertedInput: Vector[Int]): Boolean = {
    convertedInput(0) == convertedInput(2) && convertedInput(1) > convertedInput(3)
  }

  private def shipIsPlacedDownToUp(convertedInput: Vector[Int]): Boolean = {
    convertedInput(0) > convertedInput(2) && convertedInput(1) == convertedInput(3)
  }

}
