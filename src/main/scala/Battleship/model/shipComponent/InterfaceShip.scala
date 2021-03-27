package Battleship.model.shipComponent

trait InterfaceShip {

  def shipLength: Int

  def shipCoordinates: Array[Map[String, Int]]

  def status: Boolean

  def hit(x: Int, y: Int): Boolean

}
