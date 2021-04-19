package Battleship.aview.tui

import Battleship.controller.InterfaceController
import Battleship.controller.controllerComponent.events._
import Battleship.controller.controllerComponent.states.{GameState, PlayerState}
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}

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

  private def requestPlayerName(player: String): String = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext
    val responseFuture: Future[HttpResponse] = Http().singleRequest(Get("http://localhost:8080/model?getPlayerName=" + player))
    val result = Await.result(responseFuture, atMost = 10.second)
    if (result.status == StatusCodes.OK) {
      println(result)
      println(result.entity.)
      "he"
    }
    else
      player
  }

  private def gridAsString(): String = {
    controller.playerState match {
      case PlayerState.PLAYER_ONE => requestGrid("player_01", showAllShips) //controller.player_01.grid.toString(showAllShips) // @TODO http call
      case PlayerState.PLAYER_TWO => requestGrid("player_02", showAllShips) // controller.player_02.grid.toString(showAllShips) // @TODO http call
    }
  }

  private def requestGrid(player: String, showAll: Boolean): String = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext
    val responseFuture: Future[HttpResponse] = Http().singleRequest(Get("http://localhost:8080/model?getPlayerName=" + player + showAll))
    val result = Await.result(responseFuture, atMost = 10.second)
    if (result.status == StatusCodes.OK)
      result.entity.toString
    else
      ""
  }

  private def enemyGridAsString(): String = {
    controller.playerState match {
      case PlayerState.PLAYER_ONE => requestGrid("player_02", showNotAllShips) //controller.player_02.grid.toString(showNotAllShips) // @TODO http call
      case PlayerState.PLAYER_TWO => requestGrid("player_01", showNotAllShips) // controller.player_01.grid.toString(showNotAllShips) // @TODO http call
    }
  }

  private def shipSetListAsString(): String = {
    val field = new StringBuilder()
    controller.playerState match {
      case PlayerState.PLAYER_ONE =>
        //controller.player_01.shipSetList.foreach(field.append(_).append("\n")) // @TODO http call
        field.toString()
      case PlayerState.PLAYER_TWO =>
        //  controller.player_02.shipSetList.foreach(field.append(_).append("\n")) // @TODO http call
        field.toString()
    }
  }

}
