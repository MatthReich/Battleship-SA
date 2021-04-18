package Battleship

import Battleship.model.gridComponent.gridImplementation.Grid
import Battleship.model.gridComponent.strategyCollide.StrategyCollideNormal
import Battleship.model.playerComponent.InterfacePlayer
import Battleship.model.playerComponent.playerImplementation.Player
import Battleship.model.shipComponent.InterfaceShip
import Battleship.model.states.GameState
import Battleship.requestHandling.RequestHandler
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import play.api.libs.json.{JsLookupResult, JsValue, Json}

import scala.io.StdIn
import scala.util.{Failure, Success}

object AkkaHttpModel {

  def main(args: Array[String]): Unit = {

    val grid_player_01 = Grid(10, new StrategyCollideNormal, Vector[Map[String, Int]]()).initGrid()
    val grid_player_02 = Grid(10, new StrategyCollideNormal, Vector[Map[String, Int]]()).initGrid()

    var player_01: InterfacePlayer = Player("player_01", Map[Int, Int](), Vector[InterfaceShip](), grid_player_01)
    var player_02: InterfacePlayer = Player("player_02", Map[Int, Int](), Vector[InterfaceShip](), grid_player_02)

    val requestHandler = RequestHandler()

    implicit val system = ActorSystem(Behaviors.empty, "my-system")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.executionContext

    /*
    * getPlayerName
    * getPlayerShipSetList
    * getPlayerGrid
    * */

    val route = concat(
      path("model") {
        parameters("getPlayerName".optional, "getPlayerShipSetList".optional, "getPlayerGrid".optional) { (name, shipSetList, grid) =>
          var answer: String = ""
          name match {
            case Some(player) => player match {
              case "player_01" => answer += player_01.name
              case "player_02" => answer += player_02.name
              case _ => complete(StatusCodes.BadRequest)
            }
            case None =>
          }
          shipSetList match {
            case Some(player) => player match {
              case "player_01" => answer += player_01.shipSetList
              case "player_02" => answer += player_02.shipSetList
              case _ => complete(StatusCodes.BadRequest)
            }
            case None =>
          }
          grid match {
            case Some(player) => player match {
              case "player_01" => answer += player_01.grid.grid
              case "player_02" => answer += player_02.grid.grid
              case _ =>
                complete(StatusCodes.BadRequest)
            }
            case None =>
          }
          if (answer.nonEmpty) {
            complete(HttpEntity(ContentTypes.`application/json`, "" + Json.toJson(answer)))
          } else {
            complete(StatusCodes.BadRequest)
          }
        }
      },
      path("model" / "player" / "name" / "update") {
        parameters("playerName", "newPlayerName") { (player, newName) =>
          requestHandler.commandPlayerSetting(player, newName, player_01, player_02) match {
            case Success(newPlayer) =>
              player match {
                case "player_01" => player_01 = newPlayer
                case "player_02" => player_02 = newPlayer
              }
              complete(StatusCodes.OK)
            case Failure(exception) => complete(StatusCodes.custom(55, exception.getMessage, "default lul"))
          }
        }
      },
      path("model" / "player" / "grid" / "update") {
        post {
          entity(as[String]) { jsonString => {
            val json = Json.parse(jsonString)
            (json \ "player").as[String] match {
              case "player_01" =>
                player_01.grid.setField("SHIPSETTING", coordsToVectorMap(json \ "coords"))
                complete(StatusCodes.OK)
              case "player_02" =>
                player_02.grid.setField("SHIPSETTING", coordsToVectorMap(json \ "coords"))
                complete(StatusCodes.OK)
              case _ => complete(StatusCodes.BadRequest)
            }
          }
          }
        }
      },
      path("model" / "player" / "grid" / "update") {

        parameters("playerName", "gameState", "coords") { (player, gameState, coordsJs) =>
          player match {
            case "player_01" =>
              val coords = Json.toJson(coordsJs).as[Vector[Map[String, Int]]]
              player_01.grid.setField(gameState, coords)
              complete(StatusCodes.OK)
            case "player_02" =>
              complete(StatusCodes.OK)
            case _ => complete(StatusCodes.BadRequest)
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

  private def coordsToVectorMap(coordsJs: JsLookupResult): Vector[Map[String, Int]] = {
    coordsJs.result.toOption match {
      case Some(value) => return value.as[Vector[Map[String, Int]]]
      case None => return Vector[Map[String, Int]]()
    }
    Vector[Map[String, Int]]()
  }

}
