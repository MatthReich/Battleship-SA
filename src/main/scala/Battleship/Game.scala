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

  val interface: String = "0.0.0.0"
  val port: Int = 8079
  val controllerHttp: String = sys.env.getOrElse("CONTROLLERHTTPSERVER", "localhost:8081")
  val gameMode: String = sys.env.getOrElse("GAMEMODE", "local")

  def main(args: Array[String]): Unit = {

    listenTo()

    if (gameMode == "local") {
      do {
        requestNewEvent("GAMESTART")
        requestNewEvent("PLAYERCHANGED")
        println("game started, watch for tui or gui")
        println("press any button to restart the game")
        scala.io.StdIn.readLine()
      } while (true)
    } else {
      Thread.sleep(25000)
      requestNewEvent("GAMESTART")
      requestNewEvent("PLAYERCHANGED")
      println("game started, watch for tui or gui")
    }
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
    Http().singleRequest(Post(s"http://${controllerHttp}/controller/update/event", payload.toString()))
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
    Http().newServerAt(interface, port).bind(route)
    println(s"Server online at: http://${interface}:${port}/\nPress RETURN to stop...")
  }

}