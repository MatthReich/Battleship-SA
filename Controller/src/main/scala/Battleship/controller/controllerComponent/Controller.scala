package Battleship.controller.controllerComponent

import Battleship.controller.InterfaceController
import Battleship.controller.controllerComponent.commands.commandComponents.{CommandIdle, CommandPlayerSetting, CommandShipSetting}
import Battleship.controller.controllerComponent.states.GameState.GameState
import Battleship.controller.controllerComponent.states.PlayerState.PlayerState
import Battleship.controller.controllerComponent.states.{GameState, PlayerState}
import Battleship.controller.controllerComponent.utils.UndoManager
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.{Get, Post}
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import com.google.inject.Inject
import play.api.libs.json.Json

import scala.annotation.tailrec
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success, Try}

//noinspection HttpUrlsUsage
class Controller @Inject()(var gameState: GameState = GameState.PLAYERSETTING, var playerState: PlayerState = PlayerState.PLAYER_ONE) extends InterfaceController {
  private val undoManager: UndoManager = new UndoManager
  private val modelHttp: String = sys.env.getOrElse("MODELHTTPSERVER", "localhost:8080")
  private val tuiHttp: String = sys.env.getOrElse("TUIHTTPSERVER", "localhost:8082")
  private val guiHttp: String = sys.env.getOrElse("GUIHTTPSERVER", "localhost:8083")

  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  override def changeGameState(gameState: GameState): Unit = this.gameState = gameState

  override def changePlayerState(playerState: PlayerState): Unit = this.playerState = playerState

  override def redoTurn(): Unit = undoManager.undoStep()

  override def doTurn(input: String): Unit = {
    gameState match {
      case GameState.PLAYERSETTING => undoManager.doStep(new CommandPlayerSetting(input, this))
      case GameState.SHIPSETTING => undoManager.doStep(new CommandShipSetting(input, this, handleInput))
      case GameState.IDLE => undoManager.doStep(new CommandIdle(input, this, handleInput))
    }
  }

  override def save(): Unit = {
    waitForResponse(Http().singleRequest(Get(s"http://$modelHttp/model/database?request=save&gameState=" + gameState.toString.toUpperCase + "&playerState=" + playerState.toString.toUpperCase)))
    requestNewReaction("SAVED", "")
  }

  override def load(): Unit = {
    waitForResponse(Http().singleRequest(Get(s"http://$modelHttp/model/database?request=load")))
    requestNewReaction("LOADED", "")
  }

  override def requestNewReaction(event: String, message: String): Unit = {
    val payload = Json.obj(
      "event" -> event.toUpperCase,
      "message" -> message
    )
    Http().singleRequest(Post(s"http://$tuiHttp/tui/reactor", payload.toString()))
    Http().singleRequest(Post(s"http://$guiHttp/gui/reactor", payload.toString()))
  }

  override def requestChangePlayerName(player: String, newName: String): Option[Throwable] = {
    val name = if (newName == "") if (playerState == PlayerState.PLAYER_ONE) "player_01" else "player_02" else newName
    val result = waitForResponse(Http().singleRequest(Get(s"http://$modelHttp/model/player/name/update?playerName=" + player + "&newPlayerName=" + name)))
    if (result.status != StatusCodes.OK) {
      Some(new Exception("request status was: " + result.status))
    } else {
      None
    }
  }

  override def requestHandleFieldSettingShipSetting(player: String, coords: Vector[Map[String, Int]]): Option[Throwable] = {
    val payload = Json.obj(
      "player" -> player,
      "coords" -> Json.toJson(coords),
      "gameState" -> gameState.toString.toUpperCase
    )
    val result: HttpResponse = waitForResponse(Http().singleRequest(Post(s"http://$modelHttp/model/player/shipsetting/update", payload.toString())))
    if (result.status != StatusCodes.OK) {
      Some(new Exception(result.status.reason()))
    } else {
      None
    }
  }

  private def waitForResponse(future: Future[HttpResponse]): HttpResponse = {
    Await.result(future, atMost = 10.second)
  }

  override def requestShipSettingFinished(player: String): Boolean = {
    val result: HttpResponse = waitForResponse(Http().singleRequest(Get(s"http://$modelHttp/model/player/shipsetting/request?shipSettingFinished=" + player)))
    result.status == StatusCodes.OK
  }

  override def requestHandleFieldSettingIdle(player: String, coords: Vector[Map[String, Int]]): Either[Boolean, Throwable] = {
    val payload = Json.obj(
      "player" -> player,
      "coords" -> Json.toJson(coords),
      "gameState" -> gameState.toString.toUpperCase
    )
    val result: HttpResponse = waitForResponse(Http().singleRequest(Post(s"http://$modelHttp/model/player/idle/update", payload.toString())))
    if (result.status.toString() == "468 change player") {
      Left(true)
    } else if (result.status != StatusCodes.OK) {
      Right(new Exception(result.status.reason()))
    } else {
      Left(false)
    }
  }

  override def requestGameIsWon(player: String): Boolean = {
    val result: HttpResponse = waitForResponse(Http().singleRequest(Get(s"http://$modelHttp/model/player/idle/request?gameIsWon=" + player)))
    result.status == StatusCodes.OK
  }

  private def handleInput(input: String, state: Either[Int, Int]): Try[Vector[Map[String, Int]]] = {
    Try(input.split(" ").map(_.toInt)) match {
      case Success(convertedInput) =>
        if (convertedInput.exists(_.>=(10))) return Failure(new Exception("input is out of scope"))
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
