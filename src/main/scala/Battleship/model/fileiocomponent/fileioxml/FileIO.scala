package Battleship.model.fileiocomponent.fileioxml

import Battleship.model.fileiocomponent.FileIOInterface
import Battleship.model.gridcomponent.InterfaceGrid
import Battleship.model.personcomponent.InterfacePerson
import Battleship.model.statecomponent.GameState.GameState
import Battleship.model.statecomponent.PlayerState.PlayerState
import Battleship.model.statecomponent.{GameState, PlayerState}
import com.google.inject.Inject

import scala.xml.PrettyPrinter

class FileIO @Inject()(var player: InterfacePerson, var player2: InterfacePerson, var grid01: InterfaceGrid, var grid02: InterfaceGrid, ship: InterfaceShip
                      ) extends FileIOInterface {

  override def load: (InterfaceGrid, InterfaceGrid, InterfacePerson, InterfacePerson, Array[Int], Array[Int], InterfaceShip, Array[Int], Boolean, Boolean, String, GameState, PlayerState) = {
    val file = scala.xml.XML.loadFile("saveFile.xml")
    var cellNodes = (file \\ "save" \ "grid" \ "cell")
    for (cell <- cellNodes) {
      val row: Int = (cell \ "@row").text.toInt
      val col: Int = (cell \ "@col").text.toInt
      val value: Int = cell.text.trim.toInt
      grid01.setField(row, col, value)
    }
    cellNodes = (file \\ "save" \ "gridx" \ "cellx")
    for (cell <- cellNodes) {
      val row: Int = (cell \ "@rowx").text.toInt
      val col: Int = (cell \ "@colx").text.toInt
      val value: Int = cell.text.trim.toInt
      grid02.setField(row, col, value)
    }

    player.addName((file \\ "save" \ "player1").text.toString)
    player2.addName((file \\ "save" \ "player2").text.toString)

    var shipSetting: Array[Int] = Array[Int](0, 0, 0, 0)
    var shipSetting2: Array[Int] = Array[Int](0, 0, 0, 0)
    var shipCoordsSetting: Array[Int] = Array[Int](0, 0, 0, 0)

    cellNodes = (file \\ "save" \ "shipSetting" \ "array" \ "cell")
    for (cell <- cellNodes) {
      val col: Int = (cell \ "@col").text.toInt
      val value: Int = cell.text.trim.toInt
      shipSetting(col) = value
    }

    cellNodes = (file \\ "save" \ "shipSetting2" \ "array" \ "cell")
    for (cell <- cellNodes) {
      val col: Int = (cell \ "@col").text.toInt
      val value: Int = cell.text.trim.toInt
      shipSetting2(col) = value
    }

    cellNodes = (file \\ "save" \ "shipCoordsSetting" \ "array" \ "cell")
    for (cell <- cellNodes) {
      val col: Int = (cell \ "@col").text.toInt
      val value: Int = cell.text.trim.toInt
      shipCoordsSetting(col) = value
    }

    val shipSet: Boolean = (file \\ "save" \ "shipSet").text.toBoolean
    val shipDelete: Boolean = (file \\ "save" \ "shipDelete").text.toBoolean
    val lastGuess: String = (file \\ "save" \ "lastGuess").text.toString

    val gameState: GameState = (file \\ "save" \ "gameState").text.toString match {
      case "PLAYERSETTING" => GameState.PLAYERSETTING
      case "SHIPSETTING" => GameState.SHIPSETTING
      case "IDLE" => GameState.IDLE
      case "SOLVED" => GameState.SOLVED
    }
    val playerState: PlayerState = (file \\ "save" \ "playerState").text.toString match {
      case "PLAYER_ONE" => PlayerState.PLAYER_ONE
      case "PLAYER_TWO" => PlayerState.PLAYER_TWO
    }

    (grid01, grid02, player, player2, shipSetting, shipSetting2, ship, shipCoordsSetting, shipSet, shipDelete, lastGuess, gameState, playerState)
  }

  override def save(grid1: InterfaceGrid, grid2: InterfaceGrid, player: InterfacePerson, player2: InterfacePerson, shipSetting: Array[Int], shipSetting2: Array[Int], ship: InterfaceShip, shipCoordsSetting: Array[Int], shipSet: Boolean, shipDelete: Boolean, lastGuess: String, gameState: GameState, playerState: PlayerState): Unit = {
    saveString(grid1, grid2, player, player2, shipSetting, shipSetting2, ship, shipCoordsSetting, shipSet, shipDelete, lastGuess, gameState, playerState)
  }

  def saveString(grid: InterfaceGrid, grid2: InterfaceGrid, player: InterfacePerson, player2: InterfacePerson, shipSetting: Array[Int], shipSetting2: Array[Int], ship: InterfaceShip, shipCoordsSetting: Array[Int], shipSet: Boolean, shipDelete: Boolean, lastGuess: String, gameState: GameState, playerState: PlayerState): Unit = {
    import java.io._
    val pw = new PrintWriter(new File("saveFile.xml"))
    val prettyPrinter = new PrettyPrinter(120, 4)
    var xml = "<save>\n"
    xml += prettyPrinter.format(gridToXml1(grid))
    xml += "\n"
    xml += prettyPrinter.format(gridToXml2(grid2))
    xml += "\n<player1>" + player.toString + "</player1>"
    xml += "\n<player2>" + player2.toString + "</player2>"
    xml += "\n<shipSetting>" + prettyPrinter.format(arrayToXml(shipSetting)) + "</shipSetting>"
    xml += "\n<shipSetting2>" + prettyPrinter.format(arrayToXml(shipSetting2)) + "</shipSetting2>"
    xml += "\n<ship></ship>"
    xml += "\n<shipCoordsSetting>" + prettyPrinter.format(arrayToXml(shipCoordsSetting)) + "</shipCoordsSetting>"
    xml += "\n<shipSet>" + shipSet + "</shipSet>"
    xml += "\n<shipDelete>" + shipDelete + "</shipDelete>"
    xml += "\n<lastGuess>" + lastGuess + "</lastGuess>"
    xml += "\n<gameState>" + gameState + "</gameState>"
    xml += "\n<playerState>" + playerState + "</playerState>"
    xml += "\n</save>"
    pw.write(xml)
    pw.close
  }

  def arrayToXml(shipSetting: Array[Int]) = {
    <array size={shipSetting.size.toString}>
      {for {
      col <- 0 until shipSetting.size
    } yield arraytoString(shipSetting, col)}
    </array>
  }

  def arraytoString(shipSetting: Array[Int], col: Int) = {
    <cell col={col.toString}>
      {shipSetting(col).toString}
    </cell>
  }

  def gridToXml1(grid: InterfaceGrid) = {
    <grid size={grid.getSize.toString}>
      {for {
      row <- 0 until grid.getSize
      col <- 0 until grid.getSize
    } yield cellToXml1(grid, row, col)}
    </grid>
  }

  def cellToXml1(grid: InterfaceGrid, row: Int, col: Int) = {
    <cell row={row.toString} col={col.toString}>
      {grid.getValue(row, col)}
    </cell>
  }

  def gridToXml2(grid: InterfaceGrid) = {
    <gridx sizex={grid.getSize.toString}>
      {for {
      row <- 0 until grid.getSize
      col <- 0 until grid.getSize
    } yield cellToXml2(grid, row, col)}
    </gridx>
  }

  def cellToXml2(grid: InterfaceGrid, row: Int, col: Int) = {
    <cellx rowx={row.toString} colx={col.toString}>
      {grid.getValue(row, col)}
    </cellx>
  }

}
