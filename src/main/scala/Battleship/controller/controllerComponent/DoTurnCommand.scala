package Battleship.controller.controllerComponent

import Battleship.model.playerComponent.InterfacePlayer
import Battleship.model.shipComponent.shipImplemenation.Ship
import Battleship.utils.Command

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.Try

class DoTurnCommand(input: String, controller: Controller) extends Command {

  override def doStep(): Unit = {
    controller.gameState match {
      case GameState.PLAYERSETTING =>
        setPlayerName()
      case GameState.SHIPSETTING =>
        setShip()
      case GameState.IDLE =>
        setGuess()
      case GameState.SOLVED =>
    }
  }

  override def undoStep(): Unit = {}

  private def setPlayerName(): Unit = {
    controller.playerState match {
      case PlayerState.PLAYER_ONE =>
        if (input != "")
          controller.player_01 = controller.player_01.updateName(input)
        controller.playerState = PlayerState.PLAYER_TWO
        controller.publish(new PlayerChanged)
      case PlayerState.PLAYER_TWO =>
        if (input != "")
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
            if (shipSettingAllowsNewShip(coords.length, controller.player_01)) {
              val retVal = functionHelper(controller.player_01)
              controller.player_01 = retVal._1
              if (retVal._2) {
                controller.player_01 = controller.player_01.updateShipSetList(coords.length)
                handleShipSetFinishing(controller.player_01, PlayerState.PLAYER_TWO, GameState.SHIPSETTING)
              } else {
                controller.publish(new RedoTurn)
              }
            } else {
              controller.publish(new RedoTurn)
            }
          case PlayerState.PLAYER_TWO =>
            if (shipSettingAllowsNewShip(coords.length, controller.player_02)) {
              val retVal = functionHelper(controller.player_02)
              controller.player_02 = retVal._1
              if (retVal._2) {
                controller.player_02 = controller.player_02.updateShipSetList(coords.length)
                handleShipSetFinishing(controller.player_02, PlayerState.PLAYER_ONE, GameState.IDLE)
              } else {
                controller.publish(new RedoTurn)
              }
            } else {
              controller.publish(new RedoTurn)
            }
        }
      case None => controller.publish(new RedoTurn)
    }
  }

  private def handleShipSetFinishing(player: InterfacePlayer, newPlayerState: PlayerState.PlayerState, newGameState: GameState.GameState): Unit = {
    if (shipSettingFinished(player)) {
      controller.changeGameState(newGameState)
      controller.changePlayerState(newPlayerState)
      controller.publish(new PlayerChanged)
    } else {
      controller.publish(new GridUpdated)
    }
  }

  private def shipSettingFinished(player: InterfacePlayer): Boolean = {
    player.shipSetList.getOrElse(2, Int.MaxValue) == 0 && player.shipSetList.getOrElse(3, Int.MaxValue) == 0 && player.shipSetList.getOrElse(4, Int.MaxValue) == 0
  }

  private def shipSettingAllowsNewShip(coordsLength: Int, player: InterfacePlayer): Boolean = {
    player.shipSetList.getOrElse(coordsLength, Int.MinValue) > 0
  }

  private def changePlayerStats(coords: Array[mutable.Map[String, Int]])(player: InterfacePlayer): (InterfacePlayer, Boolean) = {
    val retVal = player.grid.setField(controller.gameState, coords)
    val updatedGrid = retVal._1
    val success = retVal._2
    if (success) {
      val shipNotSunk = false
      val ship = Ship(coords.length, coords, shipNotSunk)
      (player.addShip(ship).updateGrid(updatedGrid), true)
    } else {
      (player, false)
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
            controller.player_02 = functionHelper(controller.player_02)
            if (checkWinStatement(controller.player_02)) {
                controller.changeGameState(GameState.SOLVED)
                controller.publish(new GameWon)
            } else {
              controller.changePlayerState(PlayerState.PLAYER_TWO)
              controller.publish(new PlayerChanged)
            }

          case PlayerState.PLAYER_TWO =>
            controller.player_01 = functionHelper(controller.player_01)
            if (checkWinStatement(controller.player_01)) {
              controller.changeGameState(GameState.SOLVED)
              controller.publish(new GameWon)
            } else {
              controller.changePlayerState(PlayerState.PLAYER_ONE)
              controller.publish(new PlayerChanged)
            }
        }
      case None => controller.publish(new RedoTurn)
    }
  }

  private def handleGuess(x: Int, y: Int)(player: InterfacePlayer): InterfacePlayer = {
    val newPlayer = player.updateGrid(player.grid.setField(controller.gameState, Array(mutable.Map("x" -> x, "y" -> y)))._1)

    val indexes = new ListBuffer[Int]
    player.shipList.foreach(ship => indexes.addOne(ship.shipCoordinates.indexWhere(mapping => mapping.get("x").contains(x) &&
      mapping.get("y").contains(y))))
    for (i <- indexes.indices) {
      if (indexes(i) != -1) {
        val index = player.shipList.filter(ship => ship.shipCoordinates.length > indexes(i)).indexWhere(mapping => mapping.shipCoordinates(indexes(i)).get("x").contains(x) && mapping.shipCoordinates(indexes(i)).get("y").contains(y))
        return newPlayer.updateShip(index, player.shipList(index).hit(x, y))
      }
    }
    newPlayer
  }

  private def calculateCoords(size: Int)(input: String): Option[Array[mutable.Map[String, Int]]] = {
    val splitInput = input.split(" ")
    if (splitInput.length == size) {
      if (Try(splitInput.map(_.toInt)).isFailure) return None
      val convertedInput = splitInput.map(_.toInt)
      size match {
        case 2 => return Some(Array(mutable.Map("x" -> convertedInput(0), "y" -> convertedInput(1), "value" -> 0)))
        case 4 => if (checkShipFormat(convertedInput)) return Some(calculateCoordsArray(convertedInput))
      }
    }
    None
  }

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

  private def checkWinStatement(player: InterfacePlayer): Boolean = {
    !player.shipList.exists(_.status == false)
  }

}
