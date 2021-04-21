package Battleship

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Post
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import play.api.libs.json.Json

import scala.concurrent.ExecutionContextExecutor
import scala.swing.Reactor

object Game extends Reactor {

  def main(args: Array[String]): Unit = {

    listenTo()

    println("press any button to start game")
    do {
      scala.io.StdIn.readLine()
      requestNewEvent("GAMESTART")
      requestNewEvent("PLAYERCHANGED")
      println("game started,, watch for tui oder gui")
      println("press any button to restart the game")
    } while (true)

  }

  private def initNewGame(): Unit = {
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

  private def listenTo(): Unit = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val route: Route = concat(
      path("game" / "request" / "newgame") {
        post {
          entity(as[String]) { jsonString => {
            val json = Json.parse(jsonString)
            (json \ "event").as[String] match {
              case "NEWGAMEVIEW" =>
                initNewGame()
                complete(StatusCodes.OK)
            }
          }
          }
        }
      }
    )
    Http().newServerAt("localhost", 8079).bind(route)
  }

}