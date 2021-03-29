package Battleship.controller.controllerComponent

import Battleship.model.playerComponent.InterfacePlayer
import Battleship.model.shipComponent.shipImplemenation.Ship
import Battleship.utils.Command

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class DoTurnCommand(input: String, controller: Controller) extends Command {

  override def doStep(): Unit = {
    controller.gameState match {
      case GameState.SHIPSETTING =>
        setShip()
      case GameState.IDLE =>
        setGuess()
        checkWinStatement()
    }
  }

  override def undoStep(): Unit = {}

  private def setShip(): Unit = {
    val coords: Array[mutable.Map[String, Int]] = calculateCoords(4)(input)

    val functionHelper = changePlayerStats(coords) _
    controller.playerState match {
      case PlayerState.PLAYER_ONE => controller.player_01 = functionHelper(controller.player_01)
      case PlayerState.PLAYER_TWO => controller.player_02 = functionHelper(controller.player_02)
    }
  }

  private def setGuess(): Unit = {
    val coords: Array[mutable.Map[String, Int]] = calculateCoords(2)(input)
    val x = coords(0).getOrElse("x", Int.MaxValue)
    val y = coords(0).getOrElse("y", Int.MaxValue)

    val functionHelper = handleGuess(x, y) _
    controller.playerState match {
      case PlayerState.PLAYER_ONE => controller.player_01 = functionHelper(controller.player_01)
      case PlayerState.PLAYER_TWO => controller.player_02 = functionHelper(controller.player_02)
    }
  }

  private def handleGuess(x: Int, y: Int)(player: InterfacePlayer): InterfacePlayer = {
    val indexes = new ListBuffer[Int]
    player.shipList.foreach(ship => indexes.addOne(ship.shipCoordinates.indexWhere(mapping => mapping.get("x").contains(x) &&
      mapping.get("y").contains(y))))
    player.updateGrid(player.grid.setField(controller.gameState, player.shipList(indexes.head).shipCoordinates)._1).updateShip(indexes.head, player.shipList(indexes.head).hit(x, y))
  }


  private def changePlayerStats(coords: Array[mutable.Map[String, Int]])(player: InterfacePlayer): InterfacePlayer = {
    val retVal = player.grid.setField(controller.gameState, coords)
    val updatedGrid = retVal._1
    val success = retVal._2
    val shipNotSunk = false


    if (success) {
      val ship = Ship(getSize(coords), coords, shipNotSunk)
      player.addShip(ship).updateGrid(updatedGrid)
    } else {
      player
    }
  }

  private def calculateCoords(size: Int)(input: String): Array[mutable.Map[String, Int]] = {
    val splitInput = input.split(" ")
    if (splitInput.length == size) {
      val convertedInput = splitInput.map(_.toInt)

      size match {
        case 2 => return Array(mutable.Map("x" -> convertedInput(0), "y" -> convertedInput(1), "value" -> 0))
        case 4 => if (checkShipFormat(convertedInput)) return calculateCoordsArray(convertedInput)
      }
    }
    null
  }

  private def getSize(coords: Array[mutable.Map[String, Int]]): Int = coords.length

  private def checkShipFormat(splittedInputInt: Array[Int]): Boolean = {
    !((splittedInputInt(0) == splittedInputInt(2) && splittedInputInt(1) == splittedInputInt(3))
      || (!(splittedInputInt(0) == splittedInputInt(2)) && !(splittedInputInt(1) == splittedInputInt(3))))
  }

  private def calculateCoordsArray(convertedInput: Array[Int]): Array[mutable.Map[String, Int]] = {
    val coordsArray = new Array[mutable.Map[String, Int]](getShipSize(convertedInput))
    var i = 0
    for (x <- convertedInput(0) to convertedInput(2)) {
      for (y <- convertedInput(1) to convertedInput(3)) {
        coordsArray(i) = mutable.Map("x" -> x, "y" -> y, "value" -> 1)
        i += 1
      }
    }
    coordsArray
  }

  private def getShipSize(coordsShip: Array[Int]): Int = {
    if (coordsShip(0) == coordsShip(2)) {
      math.max(calcDiff(coordsShip(1), coordsShip(3)), calcDiff(coordsShip(3), coordsShip(1)))
    } else {
      math.max(calcDiff(coordsShip(0), coordsShip(2)), calcDiff(coordsShip(2), coordsShip(0)))
    }
  }

  private def calcDiff(nr1: Int, nr2: Int): Int = {
    nr1 - nr2 + 1
  }

  private def checkWinStatement(): Unit = {
    if (!controller.player_01.shipList.exists(_.status == false)) {
      controller.changeGameState(GameState.SOLVED)
      controller.publish(new GameWon)
    }
  }

}
