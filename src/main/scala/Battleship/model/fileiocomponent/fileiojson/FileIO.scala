package Battleship.model.fileiocomponent.fileiojson

import Battleship.model.fileiocomponent.FileIOInterface
import Battleship.model.gridcomponent.InterfaceGrid
import Battleship.model.gridcomponent.advancedgrid.Grid
import Battleship.model.personcomponent.InterfacePerson
import Battleship.model.statecomponent.GameState.GameState
import Battleship.model.statecomponent.PlayerState.PlayerState
import Battleship.model.statecomponent.{GameState, PlayerState}
import com.google.inject.Inject
import play.api.libs.json.{JsNumber, JsValue, Json, Writes}

import scala.io.Source

class FileIO @Inject()(var player: InterfacePerson, var player2: InterfacePerson
                      ) extends FileIOInterface {


  override def load: (InterfaceGrid, InterfaceGrid, InterfacePerson, InterfacePerson, Array[Int], Array[Int], InterfaceShip, Array[Int], Boolean, Boolean, String, GameState, PlayerState) = {
    var grid1: InterfaceGrid = null
    var grid2: InterfaceGrid = null
    val ship: Ship = null

    val source: String = Source.fromFile("saveFile.json").getLines.mkString
    val json: JsValue = Json.parse(source)
    val size = (json \\ "size").head.as[Int]
    grid1 = Grid()

    for (index <- 0 until size * size) {
      val row = (json \\ "row") (index).as[Int]
      val col = (json \\ "col") (index).as[Int]
      val value = (json \\ "valueY") (index).as[Int]
      grid1.setField(row, col, value)
    }
    grid2 = Grid()
    for (index <- 0 until size * size) {
      val row = (json \\ "rowX") (index).as[Int]
      val col = (json \\ "colX") (index).as[Int]
      val value = (json \\ "valueX") (index).as[Int]
      grid2.setField(row, col, value)
    }

    var playerString: String = (json \\ "player1").head.as[String]
    player.addName(playerString)
    playerString = (json \\ "player2").head.as[String]
    player2.addName(playerString)

    val shipSetting: Array[Int] = (json \\ "shipSetting").head.as[Array[Int]]
    val shipSetting2: Array[Int] = (json \\ "shipSetting2").head.as[Array[Int]]
    val shipCoordsSetting: Array[Int] = (json \\ "shipCoordsSet").head.as[Array[Int]]


    val shipSet: Boolean = (json \\ "shipSet").head.as[Boolean]
    val shipDelete: Boolean = (json \\ "shipDelete").head.as[Boolean]
    val lastGuess: String = (json \\ "lastGuess").head.as[String]

    val gameState: GameState = (json \\ "gameState").head.as[String] match {
      case "PLAYERSETTING" => GameState.PLAYERSETTING
      case "SHIPSETTING" => GameState.SHIPSETTING
      case "IDLE" => GameState.IDLE
      case "SOLVED" => GameState.SOLVED
    }
    val playerState: PlayerState = (json \\ "playerState").head.as[String] match {
      case "PLAYER_ONE" => PlayerState.PLAYER_ONE
      case "PLAYER_TWO" => PlayerState.PLAYER_TWO
    }

    (grid1, grid2, player, player2, shipSetting, shipSetting2, ship, shipCoordsSetting, shipSet, shipDelete, lastGuess, gameState, playerState)
  }

  override def save(grid: InterfaceGrid, grid2: InterfaceGrid, player: InterfacePerson, player2: InterfacePerson, shipSetting: Array[Int], shipSetting2: Array[Int], ship: InterfaceShip, shipCoordsSetting: Array[Int], shipSet: Boolean, shipDelete: Boolean, lastGuess: String, gameState: GameState, playerState: PlayerState): Unit = {
    import java.io._
    val pw = new PrintWriter(new File("saveFile.json"))
    pw.write(Json.prettyPrint(getAllObj(grid, grid2, player, player2, shipSetting, shipSetting2, ship, shipCoordsSetting, shipSet, shipDelete, lastGuess, gameState, playerState)))
    pw.close()
  }

  def getAllObj(grid: InterfaceGrid, grid2: InterfaceGrid, player: InterfacePerson, player2x: InterfacePerson, shipSetting: Array[Int], shipSetting2: Array[Int], shipX: InterfaceShip, shipCoordsSettingX: Array[Int], shipSetX: Boolean, shipDeleteX: Boolean, lastGuessX: String, gameStateX: GameState, playerStateX: PlayerState): JsValue = {
    val array: Array[Array[Int]] = Array(shipSetting, shipSetting2, shipCoordsSettingX)
    val players: Array[InterfacePerson] = Array(player, player2x)
    val bools: Array[Boolean] = Array(shipSetX, shipDeleteX)
    Json.toJson(List(grid1ToJson(grid), grid2ToJson(grid2), playerToJson.writes(players),
      arrayToJson.writes(array), ship.writes(shipX), boolToJson.writes(bools),
      lastGuess.writes(lastGuessX), gameState.writes(gameStateX), playerState.writes(playerStateX)))
  }

  implicit val playerToJson = new Writes[Array[InterfacePerson]] {
    override def writes(player: Array[InterfacePerson]): JsValue = Json.obj(
      "players" -> Json.obj(
        "player1" -> Json.toJson(player(0).toString),
        "player2" -> Json.toJson(player(1).toString)
      )
    )
  }

  implicit val arrayToJson = new Writes[Array[Array[Int]]] {
    override def writes(array: Array[Array[Int]]): JsValue = Json.obj(
      "arraysInt" -> Json.obj(
        "shipSetting" -> Json.toJson(array(0)),
        "shipSetting2" -> Json.toJson(array(1)),
        "shipCoordsSet" -> Json.toJson(array(2))
      )
    )
  }

  implicit val ship = new Writes[InterfaceShip] {
    def writes(ship: InterfaceShip): JsValue = Json.obj(
      "ship" -> ""
    )
  }

  implicit val boolToJson = new Writes[Array[Boolean]] {
    override def writes(bools: Array[Boolean]): JsValue = Json.obj(
      "booleans" -> Json.obj(
        "shipSet" -> Json.toJson(bools(0)),
        "shipDelete" -> Json.toJson(bools(1))
      )
    )
  }

  implicit val lastGuess = new Writes[String] {
    def writes(lastGuess: String): JsValue = Json.obj(
      "lastGuess" -> Json.toJson(lastGuess)
    )
  }

  implicit val gameState = new Writes[GameState] {
    def writes(gameState: GameState): JsValue = Json.obj(
      "gameState" -> gameState
    )
  }

  implicit val playerState = new Writes[PlayerState] {
    def writes(playerState: PlayerState): JsValue = Json.obj(
      "playerState" -> playerState
    )
  }

  implicit val grid2 = new Writes[InterfaceGrid] {
    def writes(grid2: InterfaceGrid): JsValue = grid2ToJson(grid2)
  }


  def grid1ToJson(grid: InterfaceGrid) = {
    val gridSize = grid.getSize
    Json.obj(
      "grid1" -> Json.obj(
        "size" -> JsNumber(gridSize),
        "cells" -> Json.toJson(
          for {
            row <- 0 until gridSize
            col <- 0 until gridSize
          } yield {
            Json.obj(
              "row" -> row,
              "col" -> col,
              "valueY" -> grid.getValue(row, col)
            )
          }
        )
      )
    )
  }

  def grid2ToJson(grid: InterfaceGrid) = {
    val gridSize = 10
    Json.obj(
      "grid2" -> Json.obj(
        "sizeX" -> JsNumber(gridSize),
        "cellsX" -> Json.toJson(
          for {
            row <- 0 until gridSize
            col <- 0 until gridSize
          } yield {
            Json.obj(
              "rowX" -> row,
              "colX" -> col,
              "valueX" -> grid.getValue(row, col)
            )
          }
        )
      )
    )
  }

}
