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

  def commandShipSetting(): Unit = {


  }

}
