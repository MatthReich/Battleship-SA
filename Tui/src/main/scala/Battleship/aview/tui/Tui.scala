package Battleship.aview.tui

import Battleship.controller.InterfaceController
import Battleship.controller.controllerComponent.events._
import Battleship.controller.controllerComponent.states.{GameState, PlayerState}
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import play.api.libs.json.Json

import scala.annotation.tailrec
import scala.collection.mutable
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.swing.Reactor

class Tui(controller: InterfaceController) extends Reactor {

  val showAllShips = true
  val showNotAllShips = false

  listenTo(controller)

  reactions += {
    case _: GameStart =>
      println("Yeah you play the best game in the world... probably :)")
    case _: PlayerChanged =>
      controller.gameState match {
        case GameState.PLAYERSETTING =>
          printTui("set your Name")
        case GameState.SHIPSETTING =>
          printTui("set your Ship <x y x y>\n" + gridAsString() + "\n" + "left:\n" + shipSetListAsString())
        case GameState.IDLE =>
          printTui("guess the enemy ship <x y>\n\n" + "enemy\n" + enemyGridAsString() + "you\n" + gridAsString())
        case _ =>
      }
    case _: GridUpdated =>
      controller.gameState match {
        case GameState.SHIPSETTING =>
          printTui("set your Ship <x y x y>\n" + gridAsString() + "\n" + "left:\n" + shipSetListAsString())
        case _ =>
      }
    case _: RedoTurn =>
      controller.gameState match {
        case GameState.SHIPSETTING =>
          printTui("try again .. set your Ship <x y x y>\n" + gridAsString() + "left:\n" + shipSetListAsString())
        case GameState.IDLE =>
          printTui("try again <x y>\n\n" + "enemy\n" + enemyGridAsString() + "you\n" + gridAsString())
        case _ =>
      }
    case _: TurnAgain =>
      controller.gameState match {
        case GameState.IDLE =>
          printTui("that was a hit! guess again <x y>\n\n" + "enemy\n" + enemyGridAsString() + "you\n" + gridAsString())
        case _ =>
      }
    case _: GameWon =>
      printTui("has won")
      println("<n> for new game <q> for end")
    case exception: FailureEvent => println(exception.getMessage())
    case _ =>
  }

  def tuiProcessLine(input: String): Unit = {
    if (input == "q") System.exit(0)
    else if (input == "n") controller.publish(new NewGameView)
    else if (input == "s") controller.save()
    else if (input == "l") controller.load()
    else if (input == "r") controller.redoTurn()
    else controller.doTurn(input)
  }

  private def printTui(string: String): Unit = {
    controller.playerState match {
      case PlayerState.PLAYER_ONE => println(Console.MAGENTA + requestPlayerName("player_01") + Console.RESET + " " + string)
      case PlayerState.PLAYER_TWO => println(Console.CYAN + requestPlayerName("player_02") + Console.RESET + " " + string)
    }
  }

  val size = 10
  private val water: Int = 0
  private val ship: Int = 1
  private val waterHit: Int = 2
  private val shipHit: Int = 3
  var grid: Vector[Map[String, Int]] = Vector[Map[String, Int]]()

  def toStringGrid(showAllShips: Boolean): String = toStringRek(0, 0, showAllShips, initRek())

  private def requestPlayerName(player: String): String = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext
    val responseFuture: Future[HttpResponse] = Http().singleRequest(Get("http://localhost:8080/model?getPlayerName=" + player))
    val result = Await.result(responseFuture, atMost = 10.second)
    val tmp = Json.parse(Await.result(Unmarshal(result).to[String], atMost = 10.second))
    tmp.result.toOption match {
      case Some(value) => value.toString()
      case None => player
    }
  }

  private def requestGrid(player: String, showAll: Boolean): String = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext
    val responseFuture: Future[HttpResponse] = Http().singleRequest(Get("http://localhost:8080/model?getPlayerGrid=" + player + showAll))
    val result = Await.result(responseFuture, atMost = 10.second)
    val tmp = Json.parse(Await.result(Unmarshal(result).to[String], atMost = 10.second))
    tmp.result.toOption match {
      case Some(value) => grid = value.as[Vector[Map[String, Int]]]
      case None => println("dully")
    }
    toStringGrid(showAll)
  }

  private def gridAsString(): String = {
    controller.playerState match {
      case PlayerState.PLAYER_ONE => requestGrid("player_01", showAllShips)
      case PlayerState.PLAYER_TWO => requestGrid("player_02", showAllShips)
    }
  }

  private def enemyGridAsString(): String = {
    controller.playerState match {
      case PlayerState.PLAYER_ONE => requestGrid("player_02", showNotAllShips)
      case PlayerState.PLAYER_TWO => requestGrid("player_01", showNotAllShips)
    }
  }

  private def shipSetListAsString(): String = {
    controller.playerState match {
      case PlayerState.PLAYER_ONE =>
        requestPlayerShipSetList("player_01")
      case PlayerState.PLAYER_TWO =>
        requestPlayerShipSetList("player_02")
    }
  }

  private def requestPlayerShipSetList(player: String): String = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext
    val responseFuture: Future[HttpResponse] = Http().singleRequest(Get("http://localhost:8080/model?getPlayerShipSetList=" + player))
    val result = Await.result(responseFuture, atMost = 10.second)
    val tmp = Json.parse(Await.result(Unmarshal(result).to[String], atMost = 10.second))
    tmp.toString()
  }

  @tailrec
  private def toStringRek(idx: Int, idy: Int, showAllShips: Boolean, result: mutable.StringBuilder): String = {
    if (idx == 0 && idy == size) {
      result.toString()
    } else if (idx == size) {
      val newY = idy + 1
      result ++= "\n"
      if (newY < size) {
        result ++= newY + " "
      }
      toStringRek(0, newY, showAllShips, result)
    } else {
      val fieldValue = grid(grid.indexWhere(mapping => mapping.get("x").contains(idx) && mapping.get("y").contains(idy))).getOrElse("value", Int.MaxValue)
      result ++= getFieldValueInString(fieldValue, showAllShips)
      toStringRek(idx + 1, idy, showAllShips, result)
    }
  }

  private def getFieldValueInString(fieldValue: Int, showAllShips: Boolean): String = {
    fieldValue match {
      case this.water => Console.BLUE + "  ~  " + Console.RESET
      case this.ship =>
        if (showAllShips) Console.GREEN + "  x  " + Console.RESET
        else Console.BLUE + "  ~  " + Console.RESET
      case this.shipHit => Console.RED + "  x  " + Console.RESET
      case this.waterHit => Console.BLUE + "  0  " + Console.RESET
    }
  }

  private def initRek(): mutable.StringBuilder = {
    val stringOfGrid = new mutable.StringBuilder("  ")
    var ids = 0
    while (ids < size) {
      stringOfGrid ++= "  " + ids + "  "
      ids += 1
    }
    stringOfGrid ++= "\n0 "
    stringOfGrid
  }

}
