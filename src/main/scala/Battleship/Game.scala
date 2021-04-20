package Battleship

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Post
import play.api.libs.json.Json

import scala.concurrent.ExecutionContextExecutor
import scala.swing.Reactor

object Game extends Reactor {
  //var controller: InterfaceController = initController()
  // var tui = new Tui(controller)
  // var gui = new StartGui(controller)

  // listenTo(controller) // @TODO change here to requests


  def main(args: Array[String]): Unit = {


    requestNewEvent("GAMESTART")
    requestNewEvent("PLAYERCHANGED")
    //controller.requestNewReaction("GAMESTART", "") // publish(new GameStart)
    //controller.requestNewReaction("PLAYERCHANGED", "") // publish(new PlayerChanged)

    //listenTo(controller)

    reactions += {
      case _: NewGameView => initNewGame()
    }

    // do {
    //   tui.tuiProcessLine(scala.io.StdIn.readLine())
    // } while (true)

  }

  private def initNewGame(): Unit = {
    //controller = initController()
    // tui = new Tui(controller)
    // gui = new StartGui(controller)
    requestNewEvent("GAMESTART")
    requestNewEvent("PLAYERCHANGED")
    // controller.requestNewReaction("GAMESTART", "") // publish(new GameStart)
    // controller.requestNewReaction("PLAYERCHANGED", "") // publish(new PlayerChanged)
  }

  // private def initController(): InterfaceController = {
  //   new Controller( // @TODO change here to requests
  //     GameState.PLAYERSETTING, PlayerState.PLAYER_ONE)
  // }

  private def requestNewEvent(event: String): Unit = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext
    val payload = Json.obj(
      "event" -> event.toUpperCase,
      "message" -> ""
    )
    Http().singleRequest(Post("http://localhost:8080/controller/update/event", payload.toString()))
  }

}