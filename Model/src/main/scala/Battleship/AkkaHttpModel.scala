package Battleship

import Battleship.model.databaseComponent.DaoInterface
import Battleship.model.gridComponent.InterfaceGrid
import Battleship.model.gridComponent.gridImplementation.Grid
import Battleship.model.gridComponent.strategyCollide.StrategyCollideNormal
import Battleship.model.playerComponent.InterfacePlayer
import Battleship.model.playerComponent.playerImplementation.Player
import Battleship.model.shipComponent.InterfaceShip
import Battleship.requestHandling.RequestHandler
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshal
import play.api.libs.json.{JsLookupResult, JsValue, Json}

import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.io.StdIn
import scala.util.{Failure, Success}
import com.google.inject.{Guice, Injector}

import scala.concurrent.duration.DurationInt

object AkkaHttpModel {

  val interface: String = "0.0.0.0"
  val port: Int = 8080

  val injector: Injector = Guice.createInjector(new ModelModule)
  val dataBase: DaoInterface = injector.getInstance(classOf[DaoInterface])
  dataBase.create()

  val grid_player_01: InterfaceGrid = Grid(10, new StrategyCollideNormal, Vector[Map[String, Int]]()).initGrid()
  val grid_player_02: InterfaceGrid = Grid(10, new StrategyCollideNormal, Vector[Map[String, Int]]()).initGrid()

  var player_01: InterfacePlayer = Player("player_01", Map("2" -> 2, "3" -> 1, "4" -> 1, "5" -> 2), Vector[InterfaceShip](), grid_player_01)
  var player_02: InterfacePlayer = Player("player_02", Map("2" -> 2, "3" -> 1, "4" -> 1, "5" -> 2), Vector[InterfaceShip](), grid_player_02)

  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext
  val controllerHttp: String = sys.env.getOrElse("CONTROLLERHTTPSERVER", "localhost:8081")

  val requestHandler: RequestHandler = RequestHandler()
  val route: Route = concat(
    path("model") {
      parameters("getPlayerName".optional, "getPlayerShipSetList".optional, "getPlayerGrid".optional) { (name, shipSetList, grid) =>
        var answer: JsValue = Json.toJson("")
        name match {
          case Some(player) => player match {
            case "player_01" => answer = Json.toJson(player_01.name)
            case "player_02" => answer = Json.toJson(player_02.name)
            case _ => complete(StatusCodes.BadRequest)
          }
          case None =>
        }
        shipSetList match {
          case Some(player) => player match {
            case "player_01" => answer = Json.toJson(player_01.shipSetList)
            case "player_02" => answer = Json.toJson(player_02.shipSetList)
            case _ => complete(StatusCodes.BadRequest)
          }
          case None =>
        }
        grid match {
          case Some(player) => player match {
            case "player_01true" => answer = Json.toJson(player_01.grid.grid)
            case "player_02true" => answer = Json.toJson(player_02.grid.grid)
            case "player_01false" => answer = Json.toJson(player_01.grid.grid)
            case "player_02false" => answer = Json.toJson(player_02.grid.grid)
            case _ => complete(StatusCodes.BadRequest)
          }
          case None =>
        }
        if (answer.toString() != "" && answer.toString() != "\"\"") {
          complete(HttpEntity(ContentTypes.`application/json`, "" + Json.toJson(answer)))
        } else {
          complete(StatusCodes.BadRequest)
        }
      }
    },
    path("model" / "init") {
      post {
        entity(as[String]) { string => {
          val json = Json.parse(string)
          (json \ "player").as[String] match {
            case "player_01" =>
              player_01 = player_01.updateName("player_01").updateShipSetList(payloadExtractMap(json \ "shipSetList")).updateGrid(grid_player_01)
              complete(StatusCodes.OK)
            case "player_02" =>
              player_02 = player_02.updateName("player_02").updateShipSetList(payloadExtractMap(json \ "shipSetList")).updateGrid(grid_player_02)
              complete(StatusCodes.OK)
            case _ => complete(StatusCodes.BadRequest)
          }
        }
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
          case Failure(exception) => complete(HttpResponse(StatusCodes.custom(469, exception.getMessage)))
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
    path("model" / "player" / "shipsetting" / "update") {
      post {
        entity(as[String]) { jsonString => {
          val json = Json.parse(jsonString)
          (json \ "player").as[String] match {
            case "player_01" =>
              requestHandler.commandShipSetting(coordsToVectorMap(json \ "coords"), player_01, payloadExtractGameState(json \ "gameState")) match {
                case Success(newPlayer) =>
                  player_01 = newPlayer
                  complete(HttpResponse(status = StatusCodes.OK))
                case Failure(exception) => complete(HttpResponse(StatusCodes.custom(469, exception.getMessage)))
              }
            case "player_02" =>
              requestHandler.commandShipSetting(coordsToVectorMap(json \ "coords"), player_02, payloadExtractGameState(json \ "gameState")) match {
                case Success(newPlayer) =>
                  player_02 = newPlayer
                  complete(HttpResponse(status = StatusCodes.OK))
                case Failure(exception) => complete(HttpResponse(StatusCodes.custom(469, exception.getMessage)))
              }
            case _ => complete(StatusCodes.BadRequest)
          }
        }
        }
      }
    },
    path("model" / "player" / "shipsetting" / "request") {
      parameters("shipSettingFinished") {
        case "player_01" =>
          if (!player_01.shipSetList.exists(_._2 != 0)) complete(StatusCodes.OK)
          else complete(StatusCodes.BadRequest)
        case "player_02" =>
          if (!player_02.shipSetList.exists(_._2 != 0)) complete(StatusCodes.OK)
          else complete(StatusCodes.BadRequest)
        case _ => complete(StatusCodes.BadRequest)
      }
    },
    path("model" / "player" / "idle" / "update") {
      post {
        entity(as[String]) { jsonString => {
          val json = Json.parse(jsonString)
          (json \ "player").as[String] match {
            case "player_01" =>
              requestHandler.commandIdle(coordsToVectorMap(json \ "coords"), player_01, payloadExtractGameState(json \ "gameState")) match {
                case Left(way) => way match {
                  case Left(newPlayer) => // nochmal dran
                    player_01 = newPlayer
                    complete(StatusCodes.OK)
                  case Right(newPlayer) => // player change
                    player_01 = newPlayer
                    complete(HttpResponse(StatusCodes.custom(468, "change player")))
                }
                case Right(exception) => complete(HttpResponse(StatusCodes.custom(469, exception.getMessage)))
              }
            case "player_02" =>
              requestHandler.commandIdle(coordsToVectorMap(json \ "coords"), player_02, payloadExtractGameState(json \ "gameState")) match {
                case Left(way) => way match {
                  case Left(newPlayer) => // nochmal dran
                    player_02 = newPlayer
                    complete(StatusCodes.OK)
                  case Right(newPlayer) => // player change
                    player_02 = newPlayer
                    complete(HttpResponse(StatusCodes.custom(468, "change player")))
                }
                case Right(exception) => complete(HttpResponse(StatusCodes.custom(469, exception.getMessage)))
              }
            case _ => complete(StatusCodes.BadRequest)
          }
        }
        }
      }
    },
    path("model" / "player" / "idle" / "request") {
      parameters("gameIsWon") {
        case "player_01" =>
          if (!player_01.shipList.exists(_.status == false)) complete(StatusCodes.OK)
          else complete(StatusCodes.BadRequest)
        case "player_02" =>
          if (!player_02.shipList.exists(_.status == false)) complete(StatusCodes.OK)
          else complete(StatusCodes.BadRequest)
        case _ => complete(StatusCodes.BadRequest)
      }
    },
    path("model" / "database") {
      parameters("request") {
        case "load" =>
          val newPlayer1 = dataBase.read(1)
          val newPlayer2 = dataBase.read(2)
          player_01 = Player(newPlayer1._1, newPlayer1._3, newPlayer1._4, Grid(10,new StrategyCollideNormal, newPlayer1._2))
          player_02 = Player(newPlayer2._1, newPlayer2._3, newPlayer2._4, Grid(10,new StrategyCollideNormal, newPlayer2._2))
          setControllerStates(newPlayer1._5, newPlayer1._6)
          complete(StatusCodes.OK)
        case "save" =>
          dataBase.update(1, player_01.name, player_01.grid.grid, player_01.shipSetList, player_01.shipList, requestState("getGameState"), requestState("getPlayerState"))
          dataBase.update(2, player_02.name, player_02.grid.grid, player_02.shipSetList, player_02.shipList, requestState("getGameState"), requestState("getPlayerState"))
          complete(StatusCodes.OK)
      }
    }
  )

  private def requestState(state: String): String = {
    val responseFuture: Future[HttpResponse] = Http().singleRequest(Get(s"http://${controllerHttp}/controller/request?" + state + "=state"))
    val result = Await.result(responseFuture, atMost = 10.second)
    val tmp = Json.parse(Await.result(Unmarshal(result).to[String], atMost = 10.second))
    tmp.result.toOption match {
      case Some(value) => value.as[String]
      case None => ""
    }
  }

  private def setControllerStates(gameState: String, playerState: String): Unit = {
    val responseFuture: Future[HttpResponse] = Http().singleRequest(Get(s"http://${controllerHttp}/controller/update/states?setGameState=" + gameState + "&setPlayerState=" + playerState))
    Await.result(responseFuture, atMost = 10.second)
  }

  def main(args: Array[String]): Unit = {

    val bindingFuture = Http().newServerAt(interface, port).bind(route)

    println(s"Server online at http://${interface}:${port}/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }

  private def coordsToVectorMap(coordsJs: JsLookupResult): Vector[Map[String, Int]] = {
    coordsJs.result.toOption match {
      case Some(value) => value.as[Vector[Map[String, Int]]]
      case None => Vector[Map[String, Int]]()
    }
  }

  private def payloadExtractGameState(gameStateJs: JsLookupResult): String = {
    gameStateJs.as[String]
  }

  private def payloadExtractMap(jsmap: JsLookupResult): Map[String, Int] = {
    jsmap.result.toOption match {
      case Some(_) => Map("2" -> 2, "3" -> 1, "4" -> 1, "2" -> 2)
      case None => Map("2" -> 2, "3" -> 1, "2" -> 1, "5" -> 2)
    }
  }

}
