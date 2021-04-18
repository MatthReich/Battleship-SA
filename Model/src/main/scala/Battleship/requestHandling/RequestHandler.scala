package Battleship.requestHandling

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
    handleFieldSetting(changePlayerStats(coords)(player, gameState), coords.length)
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
    player.shipSetList.getOrElse(coordsLength, Int.MinValue) > 0
  }

  private def handleFieldSetting(way: Either[InterfacePlayer, Throwable], shipLength: Int): Try[InterfacePlayer] = {
    way match {
      case Left(newPlayer) => Success(newPlayer.updateShipSetList(shipLength))
      case Right(exception) => Failure(exception)
    }
  }

}
