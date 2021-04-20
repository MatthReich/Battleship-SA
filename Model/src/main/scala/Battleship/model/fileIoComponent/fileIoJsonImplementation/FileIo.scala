package Battleship.model.fileIoComponent.fileIoJsonImplementation

import Battleship.model.fileIoComponent.InterfaceFileIo
import Battleship.model.playerComponent.InterfacePlayer
import com.google.inject.Inject
import play.api.libs.json.{JsValue, Json, Writes}

import scala.io.Source

class FileIo @Inject()() extends InterfaceFileIo {

  override def save(player_01: InterfacePlayer, player_02: InterfacePlayer, gameState: String, playerState: String): Unit = {
    import java.io._
    val pw = new PrintWriter(new File("saveFile.json"))
    pw.close()
  }


  override def load(player_01: InterfacePlayer, player_02: InterfacePlayer): (InterfacePlayer, InterfacePlayer, String, String) = {
    val rawSource = Source.fromFile("saveFile.json")
    val source: String = rawSource.getLines.mkString
    val json: JsValue = Json.parse(source)
    rawSource.close()

    val gameState = (json \\ "gameState").head.as[String].toUpperCase

    val playerState = (json \\ "playerState").head.as[String].toUpperCase

    (player_01.updateName((json \\ "name_01").head.as[String]), player_02.updateName((json \\ "name_02").head.as[String]), gameState, playerState)
  }

  implicit val playerStateToJson: Writes[String] = (playerState: String) => Json.obj(
    "playerState" -> Json.toJson(playerState.toString)
  )


}
