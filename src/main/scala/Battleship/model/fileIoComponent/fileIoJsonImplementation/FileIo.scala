package Battleship.model.fileIoComponent.fileIoJsonImplementation

import Battleship.controller.controllerComponent.Controller
import Battleship.controller.controllerComponent.events.PlayerChanged
import Battleship.controller.controllerComponent.states.GameState.GameState
import Battleship.controller.controllerComponent.states.PlayerState.PlayerState
import Battleship.model.fileIoComponent.InterfaceFileIo
import Battleship.model.playerComponent.InterfacePlayer
import com.google.inject.Inject
import play.api.libs.json.{JsValue, Json, Writes}

class FileIo @Inject()() extends InterfaceFileIo {

  override def save(player_01: InterfacePlayer, player_02: InterfacePlayer, gameState: GameState, playerState: PlayerState): Unit = {
    import java.io._
    val pw = new PrintWriter(new File("saveFile.json"))
    pw.write(Json.prettyPrint(getAllObj(player_01, player_02, gameState, playerState)))
    pw.close()
  }

  private def getAllObj(player_01: InterfacePlayer, player_02: InterfacePlayer, gameState: GameState, playerState: PlayerState): JsValue = Json.toJson(List(playerToJson.writes(Array(player_01, player_02)), playerStateToJson.writes(playerState), gameStateToJson.writes(gameState)))

  override def load(controller: Controller): Unit = {
    println("laoded")
    controller.publish(new PlayerChanged)
  }


  implicit val playerToJson: Writes[Array[InterfacePlayer]] = (player: Array[InterfacePlayer]) =>
    Json.obj(
      "players" -> Json.obj(
        "player_01" -> Json.obj(
          "name" -> Json.toJson(player(0).name),
          "shipSetList" -> Json.toJson(""),
          "shipList" -> Json.toJson(""),
          "grid" -> Json.toJson("")),
        "player_02" -> Json.obj(
          "name" -> Json.toJson(player(1).name),
          "shipSetList" -> Json.toJson(""),
          "shipList" -> Json.toJson(""),
          "grid" -> Json.toJson(""))
      )
    )


  implicit val gameStateToJson: Writes[GameState] = (gameState: GameState) => Json.obj("gameState" -> gameState)

  implicit val playerStateToJson: Writes[PlayerState] = (playerState: PlayerState) => Json.obj("playerState" -> playerState)

}
