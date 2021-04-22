package Battleship.aview.tui

import Battleship.AkkaHttpTui
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.{Get, Post}
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import play.api.libs.json.Json

import scala.annotation.tailrec
import scala.collection.mutable
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.swing.Reactor

class Tui() extends Reactor {

  val showAllShips = true
  val showNotAllShips = false
  val controllerHttp = "controller-api:8081"
  val modelHttp = "model-api:8081"
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  listenTo(AkkaHttpTui)

  reactions += {
    case _: GameStart =>
      println("Yeah you play the best game in the world... probably :)")
    case _: PlayerChanged =>
      requestState("getGameState") match {
        case "PLAYERSETTING" =>
          printTui("set your Name")
        case "SHIPSETTING" =>
          printTui("set your Ship <x y x y>\n" + gridAsString() + "\n" + "left:\n" + shipSetListAsString())
        case "IDLE" =>
          printTui("guess the enemy ship <x y>\n\n" + "enemy\n" + enemyGridAsString() + "you\n" + gridAsString())
        case _ =>
      }
    case _: GridUpdated =>
      requestState("getGameState") match {
        case "SHIPSETTING" =>
          printTui("set your Ship <x y x y>\n" + gridAsString() + "\n" + "left:\n" + shipSetListAsString())
        case _ =>
      }
    case _: RedoTurn =>
      requestState("getGameState") match {
        case "SHIPSETTING" =>
          printTui("try again .. set your Ship <x y x y>\n" + gridAsString() + "left:\n" + shipSetListAsString())
        case "IDLE" =>
          printTui("try again <x y>\n\n" + "enemy\n" + enemyGridAsString() + "you\n" + gridAsString())
        case _ =>
      }
    case _: TurnAgain =>
      requestState("getGameState") match {
        case "IDLE" =>
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
    else if (input == "n") requestGameTurn("NEWGAMEVIEW", input)
    else if (input == "s") requestGameTurn("SAVE", input)
    else if (input == "l") requestGameTurn("LOAD", input)
    else if (input == "r") requestGameTurn("REDO", input)
    else requestGameTurn("DOTURN", input)
  }

  private def requestGameTurn(event: String, input: String): Unit = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext
    val payload = Json.obj(
      "event" -> event.toUpperCase,
      "input" -> input
    )
    Http().singleRequest(Post(s"http://${controllerHttp}/controller/update", payload.toString()))
  }

  private def printTui(string: String): Unit = {
    requestState("getPlayerState") match {
      case "PLAYER_ONE" => println(Console.MAGENTA + requestPlayerName("player_01") + Console.RESET + " " + string)
      case "PLAYER_TWO" => println(Console.CYAN + requestPlayerName("player_02") + Console.RESET + " " + string)
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
    val responseFuture: Future[HttpResponse] = Http().singleRequest(Get(s"http://${modelHttp}/model?getPlayerName=" + player))
    val result = Await.result(responseFuture, atMost = 10.second)
    val tmp = Json.parse(Await.result(Unmarshal(result).to[String], atMost = 10.second))
    tmp.result.toOption match {
      case Some(value) => value.as[String]
      case None => player
    }
  }

  private def requestGrid(player: String, showAll: Boolean): String = {
    val responseFuture: Future[HttpResponse] = Http().singleRequest(Get(s"http://${modelHttp}/model?getPlayerGrid=" + player + showAll))
    val result = Await.result(responseFuture, atMost = 10.second)
    val tmp = Json.parse(Await.result(Unmarshal(result).to[String], atMost = 10.second))
    tmp.result.toOption match {
      case Some(value) => grid = value.as[Vector[Map[String, Int]]]
      case None => println("dully")
    }
    toStringGrid(showAll)
  }

  private def requestState(state: String): String = {
    val responseFuture: Future[HttpResponse] = Http().singleRequest(Get(s"http://${controllerHttp}/controller/request?" + state + "=state"))
    val result = Await.result(responseFuture, atMost = 10.second)
    val tmp = Json.parse(Await.result(Unmarshal(result).to[String], atMost = 10.second))
    tmp.result.toOption match {
      case Some(value) => value.as[String]
      case None => ""
    }
  }

  private def gridAsString(): String = {
    requestState("getPlayerState") match {
      case "PLAYER_ONE" => requestGrid("player_01", showAllShips)
      case "PLAYER_TWO" => requestGrid("player_02", showAllShips)
    }
  }

  private def enemyGridAsString(): String = {
    requestState("getPlayerState") match {
      case "PLAYER_ONE" => requestGrid("player_02", showNotAllShips)
      case "PLAYER_TWO" => requestGrid("player_01", showNotAllShips)
    }
  }

  private def requestPlayerShipSetList(player: String): String = {
    val responseFuture: Future[HttpResponse] = Http().singleRequest(Get(s"http://${modelHttp}/model?getPlayerShipSetList=" + player))
    val result = Await.result(responseFuture, atMost = 10.second)
    val tmp = Json.parse(Await.result(Unmarshal(result).to[String], atMost = 10.second))
    tmp.toString()
  }

  private def shipSetListAsString(): String = {
    requestState("getPlayerState") match {
      case "PLAYER_ONE" =>
        requestPlayerShipSetList("player_01")
      case "PLAYER_TWO" =>
        requestPlayerShipSetList("player_02")
    }
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
