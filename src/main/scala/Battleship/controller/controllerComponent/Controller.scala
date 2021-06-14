package Battleship.controller.controllerComponent

import Battleship.controller.ControllerInterface
import Battleship.controller.controllerComponent.states.PlayerStates
import Battleship.controller.controllerComponent.states.GameStates
import scala.swing.Publisher
import com.google.inject.{Guice, Injector}
import Battleship.GameModul
import Battleship.model.playerComponent.PlayerInterface
import Battleship.model.playerComponent.playerImplementation.Player
import Battleship.utils.UndoManager
import Battleship.controller.controllerComponent.commands.{CommandIdle, CommandPlayerSetting, CommandShipSetting}
import scala.util.{Failure, Success, Try}
import scala.annotation.tailrec

case class Controller(
    var player_01: PlayerInterface = Player(),
    var player_02: PlayerInterface = Player(),
    var gameState: GameStates = GameStates.PLAYERSETTING,
    var playerState: PlayerStates = PlayerStates.PLAYER_ONE)
    extends ControllerInterface, Publisher:
    private val undoManager = new UndoManager

    override def updatePlayer(oldPlayer: String, newPlayer: PlayerInterface) =
        oldPlayer match
            case "player_01" => this.player_01 = newPlayer
            case "player_02" => this.player_02 = newPlayer

    override def changeGameState(gameState: GameStates): Unit                = this.gameState = gameState

    override def changePlayerState(playerState: PlayerStates): Unit = this.playerState = playerState

    override def doTurn(input: String): Unit                                                       = gameState match
        case GameStates.PLAYERSETTING => undoManager.doStep(new CommandPlayerSetting(input, this))
        case GameStates.SHIPSETTING   => undoManager.doStep(new CommandShipSetting(input, this, handleInput))
        case GameStates.IDLE          => undoManager.doStep(new CommandIdle(input, this, handleInput))
        case _                        => println("internal error")

    private def handleInput(input: String, state: Either[Int, Int]): Try[Vector[Map[String, Int]]] =
        Try(input.split(" ").map(_.toInt)) match
            case Success(convertedInput) =>
                if convertedInput.exists(_.>=(player_01.grid.size)) then
                    return Failure(new Exception("input is out of scope"))
                state match {
                    case Left(lengthOfArguments)  => calculateCoords(lengthOfArguments, convertedInput.toVector)
                    case Right(lengthOfArguments) => calculateCoords(lengthOfArguments, convertedInput.toVector)
                }
            case Failure(_)              => Failure(new Exception("failed to convert input into ints"))

    private def calculateCoords(expectedLength: Int, convertedInput: Vector[Int]): Try[Vector[Map[String, Int]]] =
        if convertedInput.length != expectedLength then
            Failure(new Exception(
                "wrong amount of arguments: expected " + expectedLength + " but got " + convertedInput.length + "!"))
        else if (expectedLength == 2)
            Success(Vector(Map("x" -> convertedInput(0), "y" -> convertedInput(1), "value" -> 0)))
        else if (checkShipFormat(convertedInput)) calculateCoordsMapping(convertedInput)
        else Failure(new Exception("coords are not in a line"))
        end if

    private def checkShipFormat(convertedInput: Vector[Int]): Boolean =
        !((convertedInput(0) == convertedInput(2) && convertedInput(1) == convertedInput(3)) || (!(convertedInput(
            0) == convertedInput(2)) && !(convertedInput(1) == convertedInput(3))))

    private def calculateCoordsMapping(convertedInput: Vector[Int]): Try[Vector[Map[String, Int]]] =
        if shipIsPlacedLeftToRight(convertedInput) || shipIsPlacedUpToDown(convertedInput) then calculateCoordsMappingRec(
            convertedInput(0),
            convertedInput(2),
            convertedInput(1),
            convertedInput(3),
            Vector[Map[String, Int]]())
        else if shipIsPlacedRightToLeft(convertedInput) then calculateCoordsMappingRec(
            convertedInput(0),
            convertedInput(2),
            convertedInput(3),
            convertedInput(1),
            Vector[Map[String, Int]]())
        else if shipIsPlacedDownToUp(convertedInput) then calculateCoordsMappingRec(
            convertedInput(2),
            convertedInput(0),
            convertedInput(3),
            convertedInput(1),
            Vector[Map[String, Int]]())
        else Failure(new Exception("something strange happened to your coords .. maybe try rice"))
        end if

    private def shipIsPlacedLeftToRight(convertedInput: Vector[Int]): Boolean =
        convertedInput(0) == convertedInput(2) && convertedInput(1) < convertedInput(3)

    private def shipIsPlacedUpToDown(convertedInput: Vector[Int]): Boolean =
        convertedInput(0) < convertedInput(2) && convertedInput(1) == convertedInput(3)

    private def shipIsPlacedRightToLeft(convertedInput: Vector[Int]): Boolean =
        convertedInput(0) == convertedInput(2) && convertedInput(1) > convertedInput(3)

    private def shipIsPlacedDownToUp(convertedInput: Vector[Int]): Boolean =
        convertedInput(0) > convertedInput(2) && convertedInput(1) == convertedInput(3)

    @tailrec
    private def calculateCoordsMappingRec(
        startX: Int,
        endX: Int,
        startY: Int,
        endY: Int,
        result: Vector[Map[String, Int]]): Try[Vector[Map[String, Int]]] =
        if startX > endX && startY == endY || startX == endX && startY > endY then Success(result)
        else if startX == endX then calculateCoordsMappingRec(
            startX,
            endX,
            startY + 1,
            endY,
            result.appended(Map("x" -> startX, "y" -> startY, "value" -> 1)))
        else if startY == endY then calculateCoordsMappingRec(
            startX + 1,
            endX,
            startY,
            endY,
            result.appended(Map("x" -> startX, "y" -> startY, "value" -> 1)))
        else Failure(new Exception("cannot calculate coords"))
        end if

    def load(): Unit = ???

    def redoTurn(): Unit = ???

    def save(): Unit = ???
