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

object AkkaHttpTui {

  val publisherTui = new PublisherTui
  val tui = new Tui()

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
                publisherTui.publish(new GameStart)
                complete(StatusCodes.OK)
              case "PLAYERCHANGED" =>
                publisherTui.publish(new PlayerChanged)
                complete(StatusCodes.OK)
              case "GRIDUPDATE" =>
                publisherTui.publish(new GridUpdated)
                complete(StatusCodes.OK)
              case "REDOTURN" =>
                publisherTui.publish(new RedoTurn)
                complete(StatusCodes.OK)
              case "TURNAGAIN" =>
                publisherTui.publish(new TurnAgain)
                complete(StatusCodes.OK)
              case "GAMEWON" =>
                publisherTui.publish(new GameWon)
                complete(StatusCodes.OK)
              case "FAILUREEVENT" =>
                publisherTui.publish(new FailureEvent((json \ "message").as[String]))
                complete(StatusCodes.OK)
              case _ => complete(StatusCodes.BadRequest)
            }
          }
          }
        }
      }
    )

    Http().newServerAt("localhost", 8082).bind(route)

    do {
      tui.tuiProcessLine(scala.io.StdIn.readLine())
    } while (true)

  }
}
