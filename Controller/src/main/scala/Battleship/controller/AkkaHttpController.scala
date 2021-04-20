package Battleship.controller

import Battleship.controller.controllerComponent.Controller
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import play.api.libs.json.{JsLookupResult, JsValue, Json}

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn
import scala.swing.event.Event

object AkkaHttpController {

  val controller = new Controller()

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val route = concat(
      path("controller" / "request") {
        parameters("getGameState".optional, "getPlayerState".optional) { (gameState, playerState) =>
          var answer: JsValue = Json.toJson("")
          gameState match {
            case Some(_) => answer = Json.toJson(controller.gameState.toString.toUpperCase)
            case None =>
          }
          playerState match {
            case Some(_) => answer = Json.toJson(controller.playerState.toString.toUpperCase)
            case None =>
          }
          if (answer.toString() != "") {
            complete(HttpEntity(ContentTypes.`application/json`, "" + Json.toJson(answer)))
          } else {
            complete(StatusCodes.BadRequest)
          }
        }
      },
      path("controller" / "update") {
        post {
          entity(as[String]) { string => {
            val json = Json.parse(string)
            (json \ "event").as[String] match {
              case "DOTURN" =>
                controller.doTurn(payloadExtractInput(json \ "input"))
                complete(StatusCodes.OK)
              case "SAVE" =>
                controller.save()
                complete(StatusCodes.OK)
              case "LOAD" =>
                controller.load()
                complete(StatusCodes.OK)
              case "REDO" =>
                controller.redoTurn()
                complete(StatusCodes.OK)
              case "NEWGAMEVIEW" =>
                class NewGameView extends Event {}
                controller.publish(new NewGameView)
                controller.requestNewReaction("NEWGAMEVIEW", "")
                complete(StatusCodes.OK)
              case _ => complete(StatusCodes.BadRequest)
            }
          }
          }
        }
        complete(StatusCodes.OK)
      },
      path("controller" / "update" / "event") {
        post {
          entity(as[String]) { string => {
            val json = Json.parse(string)
            (json \ "event").as[String] match {
              case value =>
                controller.requestNewReaction(value, "")
                complete(StatusCodes.OK)
            }
          }
          }
        }
      }
    )

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }

  private def payloadExtractInput(jsinput: JsLookupResult): String = {
    jsinput.result.toOption match {
      case Some(value) => value.toString()
      case None => ""
    }
  }

}
