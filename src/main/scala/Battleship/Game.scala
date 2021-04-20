package Battleship

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Post
import play.api.libs.json.Json

import scala.concurrent.ExecutionContextExecutor
import scala.swing.Reactor

object Game extends Reactor {
  // init controller / model / and so
  // listenTo(controller) // @TODO change here to akka publisher


  def main(args: Array[String]): Unit = {


    requestNewEvent("GAMESTART")
    requestNewEvent("PLAYERCHANGED")

    //listenTo(controller) // @TODO change here to akka publisher

    reactions += {
      case _: NewGameView => initNewGame()
    }

  }

  private def initNewGame(): Unit = {
    //controller = initController()   // @TODO change here to akka requests
    // tui = new Tui(controller)      // @TODO change here to akka requests
    // gui = new StartGui(controller) // @TODO change here to akka requests
    requestNewEvent("GAMESTART")
    requestNewEvent("PLAYERCHANGED")
  }

  private def requestNewEvent(event: String): Unit = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext
    val payload = Json.obj(
      "event" -> event.toUpperCase,
      "message" -> ""
    )
    Http().singleRequest(Post("http://localhost:8081/controller/update/event", payload.toString()))
  }

}