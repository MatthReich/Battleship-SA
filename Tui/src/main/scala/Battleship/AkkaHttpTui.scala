package Battleship

import Battleship.aview.tui._
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn

object AkkaHttpTui {

  val publisherTui = new PublisherTui

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext


    val route: Route = concat(
      path("tui" / "reactor") {
        post {
          entity(as[String]) { jsonString => {
            val json = Json.parse(jsonString)
            println(publisherTui.reactions)
            (json \ "event").as[String] match {
              case "GAMESTART" =>
                publisherTui.publish(new GameStart)
                complete(StatusCodes.OK)
              case "PLAYERCHANGED" =>
                publisherTui.publish(new PlayerChanged)
                complete(StatusCodes.OK)
              case "GRIDUPDATED" =>
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
                publisherTui.publish(new FailureEvent("test"))
                complete(StatusCodes.OK)
              case _ => complete(StatusCodes.BadRequest)
            }
          }

          }
        }
      }
    )

    val bindingFuture: Future[Http.ServerBinding] = Http().newServerAt("localhost", 8080).bind(route)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
