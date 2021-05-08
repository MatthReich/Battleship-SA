package Battleship.controller

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import scala.concurrent.ExecutionContextExecutor

class ControllerMock {
  val interface: String = "0.0.0.0"
  val port: Int = 8080

  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  val route: Route = concat(
    path("model" / "init") {
      complete(StatusCodes.OK)
    },
    path("model" / "player" / "name" / "update") {
      parameters("playerName", "newPlayerName") { (player, newName) =>
        complete(StatusCodes.OK)
      }
    },
    path("model" / "player" / "grid" / "update") {
      post {
        complete(StatusCodes.OK)
      }
    },
    path("model" / "player" / "shipsetting" / "update") {
      complete(StatusCodes.OK)
    },
    path("model" / "player" / "shipsetting" / "request") {
      parameters("shipSettingFinished") { dummy =>
        complete(StatusCodes.OK)
      }
    },
    path("model" / "player" / "idle" / "update") {
      post {
        complete(StatusCodes.OK)
      }
    },
    path("model" / "player" / "idle" / "request") {
      parameters("gameIsWon") { dummy =>
        complete(StatusCodes.OK)
      }
    },
    path("model" / "database") {
      parameters("request") { dummy =>
        complete(StatusCodes.OK)
      }
    }
  )

  Http().newServerAt(interface, port).bind(route)

}

