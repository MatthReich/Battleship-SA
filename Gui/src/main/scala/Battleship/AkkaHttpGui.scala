package Battleship

import Battleship.aview.gui._
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import play.api.libs.json.Json

import scala.concurrent.ExecutionContextExecutor
import scala.swing.Publisher

object AkkaHttpGui extends Publisher {

  val startGui = new StartGui()

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext


    val route: Route = concat(
      path("gui" / "reactor") {
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

    Http().newServerAt("localhost", 8083).bind(route)

  }
}
