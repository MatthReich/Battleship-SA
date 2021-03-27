package Battleship.model.shipComponent

import scala.collection.mutable

trait InterfaceShip {

  def shipLength: Int

  def shipCoordinates: Array[mutable.Map[String, Int]]

  def status: Boolean

  def hit(x: Int, y: Int): InterfaceShip

}
