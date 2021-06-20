package Battleship.requestHandling

import Battleship.model.gridComponent.InterfaceGrid
import Battleship.model.playerComponent.InterfacePlayer
import Battleship.model.shipComponent.shipImplemenation.Ship

import scala.util.{Failure, Success, Try}

case class RequestHandler() {
  val shipNotSunk = false


  def commandPlayerSetting(player: String, newName: String, player_01: InterfacePlayer, player_02: InterfacePlayer): Try[InterfacePlayer] = {
    player match {
      case "player_01" => Success(player_01.updateName(newName))
      case "player_02" => Success(player_02.updateName(newName))
      case _ => Failure(new Exception("name could not be changed"))
    }
  }

  def commandShipSetting(coords: Vector[Map[String, Int]], player: InterfacePlayer, gameState: String): Try[InterfacePlayer] = {
    handleFieldSettingShipSetting(changePlayerStats(coords)(player, gameState), coords.length)
  }

  private def handleFieldSettingShipSetting(way: Either[InterfacePlayer, Throwable], shipLength: Int): Try[InterfacePlayer] = {
    way match {
      case Left(newPlayer) => Success(newPlayer.updateShipSetList(shipLength))
      case Right(exception) => Failure(exception)
    }
  }

  private def changePlayerStats(coords: Vector[Map[String, Int]])(player: InterfacePlayer, gameState: String): Either[InterfacePlayer, Throwable] = {
    if (!shipSettingAllowsNewShip(coords.length, player)) return Right(new Exception("no more ships of this length can be placed"))
    player.grid.setField(gameState, coords) match {
      case Left(_) => Right(new Exception("there is already a ship placed"))
      case Right(value) => value match {
        case Failure(exception) => Right(exception)
        case Success(updatedGrid) => val ship = Ship(coords.length, coords, shipNotSunk)
          Left(player.addShip(ship).updateGrid(updatedGrid))
      }
    }
  }

  private def shipSettingAllowsNewShip(coordsLength: Int, player: InterfacePlayer): Boolean = {
    player.shipSetList.getOrElse(coordsLength.toString, Int.MinValue) > 0
  }

  def commandIdle(coords: Vector[Map[String, Int]], player: InterfacePlayer, gameState: String): Either[Either[InterfacePlayer, InterfacePlayer], Throwable] = {
    handleFieldSettingIdle(handleGuess(coords)(player, gameState))
  }

  private def handleFieldSettingIdle(tryWay: Try[Either[InterfacePlayer, InterfacePlayer]]): Either[Either[InterfacePlayer, InterfacePlayer], Throwable] = {
    tryWay match {
      case Failure(exception) => Right(exception)
      case Success(way) => Left(way)
    }
  }

  private def handleGuess(coords: Vector[Map[String, Int]])(player: InterfacePlayer, gameState: String): Try[Either[InterfacePlayer, InterfacePlayer]] = {
    player.grid.setField(gameState, coords) match {
      case Left(value) => doSame(value, player, coords.head.getOrElse("x", Int.MaxValue), coords.head.getOrElse("y", Int.MaxValue))
      case Right(value) => doSame(value, player, coords.head.getOrElse("x", Int.MaxValue), coords.head.getOrElse("y", Int.MaxValue))
    }
  }

  private def doSame(value: Try[InterfaceGrid], player: InterfacePlayer, x: Int, y: Int): Try[Either[InterfacePlayer, InterfacePlayer]] = {
    value match {
      case Success(value) =>
        val newPlayer = player.updateGrid(value)
        for (ship <- newPlayer.shipList) yield ship.hit(x, y) match {
          case Success(newShip) => return Success(Left(newPlayer.updateShip(ship, newShip)))
          case _ =>
        }
        Success(Right(newPlayer))
      case Failure(exception) => Failure(exception)
    }
  }

}
