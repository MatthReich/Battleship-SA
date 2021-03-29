package Battleship.controller.controllerComponent

import Battleship.model.playerComponent.InterfacePlayer
import Battleship.model.shipComponent.shipImplemenation.Ship
import Battleship.utils.Command

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class DoTurnCommand(input: String, controller: Controller) extends Command {

  override def doStep(): Unit = {
    controller.gameState match {
      case GameState.PLAYERSETTING =>
        setPlayerName()
      case GameState.SHIPSETTING =>
        setShip()
      case GameState.IDLE =>
        setGuess()
        checkWinStatement()
    }
  }

  override def undoStep(): Unit = {}

  private def setPlayerName(): Unit = {
    controller.playerState match {
      case PlayerState.PLAYER_ONE =>
        controller.player_01 = controller.player_01.updateName(input)
        controller.playerState = PlayerState.PLAYER_TWO
        controller.publish(new PlayerChanged)
      case PlayerState.PLAYER_TWO =>
        controller.player_02 = controller.player_02.updateName(input)
        controller.playerState = PlayerState.PLAYER_ONE
        controller.gameState = GameState.SHIPSETTING
        controller.publish(new PlayerChanged)
    }
  }

  private def setShip(): Unit = {
    val retVal = calculateCoords(4)(input)
    retVal match {
      case Some(coords) =>
        val functionHelper = changePlayerStats(coords) _
        controller.playerState match {
          case PlayerState.PLAYER_ONE =>
            val retVal = functionHelper(controller.player_01)
            controller.player_01 = retVal._1
            if (retVal._2) {
              controller.publish(new GridUpdated)
              controller.changePlayerState(PlayerState.PLAYER_TWO)
              controller.publish(new PlayerChanged)
            }
          case PlayerState.PLAYER_TWO =>
            val retVal = functionHelper(controller.player_02)
            controller.player_02 = retVal._1
            if (retVal._2) {
              controller.publish(new GridUpdated)
              controller.changeGameState(GameState.IDLE)
              controller.changePlayerState(PlayerState.PLAYER_ONE)
              controller.publish(new PlayerChanged)
            }
        }
      case None => controller.publish(new RedoTurn)
    }
  }

  private def setGuess(): Unit = {
    val retVal = calculateCoords(2)(input)
    retVal match {
      case Some(coords) =>
        val x = coords(0).getOrElse("x", Int.MaxValue)
        val y = coords(0).getOrElse("y", Int.MaxValue)

        val functionHelper = handleGuess(x, y) _
        controller.playerState match {
          case PlayerState.PLAYER_ONE =>
            controller.player_01 = functionHelper(controller.player_01)
            controller.changePlayerState(PlayerState.PLAYER_TWO)
            controller.publish(new PlayerChanged)
          case PlayerState.PLAYER_TWO =>
            controller.player_02 = functionHelper(controller.player_02)
            controller.changePlayerState(PlayerState.PLAYER_ONE)
            controller.publish(new PlayerChanged)

        }
      case None => controller.publish(new RedoTurn)
    }
  }

  private def handleGuess(x: Int, y: Int)(player: InterfacePlayer): InterfacePlayer = {
    //@TODO needs update !!!!
    val indexes = new ListBuffer[Int]
    player.shipList.foreach(ship => indexes.addOne(ship.shipCoordinates.indexWhere(mapping => mapping.get("x").contains(x) &&
      mapping.get("y").contains(y)))) // -> Index des x y paares eines schiffes innerhalb der coordinaten
    if (indexes.head == -1) { // wenn es kein schiff gibt einfach den wert ändern
      player.updateGrid(player.grid.setField(controller.gameState, Array(mutable.Map("x" -> x, "y" -> y)))._1)
    } else  { // ansonsten grid und schiff ändern
      player.updateGrid(player.grid.setField(controller.gameState, Array(mutable.Map("x" -> x, "y" -> y)))._1).updateShip(indexes.head, player.shipList.head.hit(x, y))
    }
  }

  private def changePlayerStats(coords: Array[mutable.Map[String, Int]])(player: InterfacePlayer): (InterfacePlayer, Boolean) = {
    val retVal = player.grid.setField(controller.gameState, coords)
    val updatedGrid = retVal._1
    val success = retVal._2
    if (success) {
      val shipNotSunk = false
      val ship = Ship(getSize(coords), coords, shipNotSunk)
      (player.addShip(ship).updateGrid(updatedGrid), true)
    } else {
      (player, false)
    }
  }

  private def calculateCoords(size: Int)(input: String): Option[Array[mutable.Map[String, Int]]] = {
    val splitInput = input.split(" ")
    if (splitInput.length == size) {
      val convertedInput = splitInput.map(_.toInt)

      size match {
        case 2 => return Some(Array(mutable.Map("x" -> convertedInput(0), "y" -> convertedInput(1), "value" -> 0)))
        case 4 => if (checkShipFormat(convertedInput)) return Some(calculateCoordsArray(convertedInput))
      }
    }
    None
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
