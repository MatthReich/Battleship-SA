package Battleship.model

import Battleship.model.gridComponent.gridImplementation.Grid
import Battleship.model.gridComponent.strategyCollide.StrategyCollideNormal
import Battleship.model.playerComponent.playerImplementation.Player
import Battleship.model.shipComponent.InterfaceShip
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import play.api.libs.json.{JsValue, Json}

import scala.io.StdIn

object AkkaHttpModel {

  def main(args: Array[String]): Unit = {

    val grid_player_01 = Grid(10, new StrategyCollideNormal, Vector[Map[String, Int]]()).initGrid()
    val grid_player_02 = Grid(10, new StrategyCollideNormal, Vector[Map[String, Int]]()).initGrid()

    val player_01 = Player("player_01",Map[Int, Int](),Vector[InterfaceShip](), grid_player_01)
    val player_02 = Player("player_02",Map[Int, Int](),Vector[InterfaceShip](), grid_player_02)

    implicit val system = ActorSystem(Behaviors.empty, "my-system")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.executionContext

    val route = concat(
      path("model") {
        parameters("getPlayer"){ (player)=>
          player match {
            case "player_01" => complete(HttpEntity(ContentTypes.`application/json`, ""+
              Json.toJson(player_01.name,player_01.shipSetList, player_01.grid.grid)))
            case "player_02" => complete(HttpEntity(ContentTypes.`application/json`, ""+
              Json.toJson(player_02.name,player_02.shipSetList, player_02.grid.grid)))
            case _ => complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Failed"))
          }
        }
      },
    )

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
