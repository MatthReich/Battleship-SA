package Battleship.model.fileIoComponent.fileIoJsonImplementation

import Battleship.controller.controllerComponent.Controller
import Battleship.controller.controllerComponent.events.PlayerChanged
import Battleship.controller.controllerComponent.states.GameState.GameState
import Battleship.controller.controllerComponent.states.PlayerState.PlayerState
import Battleship.controller.controllerComponent.states.{GameState, PlayerState}
import Battleship.model.fileIoComponent.InterfaceFileIo
import Battleship.model.playerComponent.InterfacePlayer
import com.google.inject.Inject
import play.api.libs.json.{JsValue, Json, Writes}

import scala.io.Source

class FileIo @Inject()() extends InterfaceFileIo {

  override def save(player_01: InterfacePlayer, player_02: InterfacePlayer, gameState: GameState, playerState: PlayerState): Unit = {
    import java.io._
    val pw = new PrintWriter(new File("saveFile.json"))
    pw.write(Json.prettyPrint(getAllObj(player_01, player_02, gameState, playerState)))
    pw.close()
  }

  implicit val gameStateToJson: Writes[GameState] = (gameState: GameState) => Json.obj(
    "gameState" -> Json.toJson(gameState.toString)
  )
  implicit val playerToJson: Writes[Vector[InterfacePlayer]] = (player: Vector[InterfacePlayer]) =>
    Json.obj(
      "players" -> Json.obj(
        "name_01" -> Json.toJson(player(0).name),
        "shipSetList_01" -> Json.toJson(""),
        "shipList_01" -> Json.toJson(""),
        "grid_01" -> Json.toJson("")),
      "name_02" -> Json.toJson(player(1).name),
      "shipSetList_02" -> Json.toJson(""),
      "shipList_02" -> Json.toJson(""),
      "grid_02" -> Json.toJson(""))

  override def load(controller: Controller): Unit = {
    val rawSource = Source.fromFile("saveFile.json")
    val source: String = rawSource.getLines.mkString
    val json: JsValue = Json.parse(source)
    rawSource.close()

    controller.player_01.updateName((json \\ "name_01").head.as[String])
    controller.player_02.updateName((json \\ "name_02").head.as[String])

    controller.gameState = (json \\ "gameState").head.as[String] match {
      case "PLAYERSETTING" => GameState.PLAYERSETTING
      case "SHIPSETTING" => GameState.SHIPSETTING
      case "IDLE" => GameState.IDLE
      case "SOLVED" => GameState.SOLVED
    }

    controller.playerState = (json \\ "playerState").head.as[String] match {
      case "PLAYER_ONE" => PlayerState.PLAYER_ONE
      case "PLAYER_TWO" => PlayerState.PLAYER_TWO
    }
    controller.publish(new PlayerChanged)
  }

  implicit val playerStateToJson: Writes[PlayerState] = (playerState: PlayerState) => Json.obj(
    "playerState" -> Json.toJson(playerState.toString)
  )

  private def getAllObj(player_01: InterfacePlayer, player_02: InterfacePlayer, gameState: GameState, playerState: PlayerState): JsValue = {
    Json.toJson(List(playerToJson.writes(Vector(player_01, player_02)), playerStateToJson.writes(playerState), gameStateToJson.writes(gameState)))
  }

}
