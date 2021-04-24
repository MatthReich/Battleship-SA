package Battleship

import Battleship.aview.tui._
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import play.api.libs.json.Json

import scala.concurrent.ExecutionContextExecutor
import scala.swing.Publisher

object AkkaHttpTui extends Publisher {

  val tui = new Tui()
  val interface: String = "0.0.0.0"
  val port: Int = 8082

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext


    val route: Route = concat(
      path("tui" / "reactor") {
        post {
          entity(as[String]) { jsonString => {
            val json = Json.parse(jsonString)
            (json \ "event").as[String] match {
              case "GAMESTART" =>
                publish(new GameStart)
                complete(StatusCodes.OK)
              case "PLAYERCHANGED" =>
                publish(new PlayerChanged)
                complete(StatusCodes.OK)
              case "GRIDUPDATE" =>
                publish(new GridUpdated)
                complete(StatusCodes.OK)
              case "REDOTURN" =>
                publish(new RedoTurn)
                complete(StatusCodes.OK)
              case "TURNAGAIN" =>
                publish(new TurnAgain)
                complete(StatusCodes.OK)
              case "GAMEWON" =>
                publish(new GameWon)
                complete(StatusCodes.OK)
              case "FAILUREEVENT" =>
                publish(new FailureEvent((json \ "message").as[String]))
                complete(StatusCodes.OK)
              case _ => complete(StatusCodes.BadRequest)
            }
          }
          }
        }
      }
    )

    Http().newServerAt(interface, port).bind(route)
    println(s"Server online at: http://${interface}:${port}/\n")

    do {
      tui.tuiProcessLine(scala.io.StdIn.readLine())
    } while (true)

  }
}
