package Battleship.model.shipcomponent.advancedShip

class Ship(val shipLength: Int, var shipCoordinates: Array[scala.collection.mutable.Map[String, Int]], var status: Boolean) {

  def hit(x: Int, y: Int): Unit = {
    shipCoordinates.foreach(m => if(m.get("x").contains(x) && m.get("y").contains(y)) { m ("value") = 0 })
    if (isSunk)
      status = true
  }

  def isSunk: Boolean = {
    var x = 0
    shipCoordinates.foreach(m => if(m.get("value").contains(1)) { x+=1 })
    if(x != 0) { false }
    else true
  }
}