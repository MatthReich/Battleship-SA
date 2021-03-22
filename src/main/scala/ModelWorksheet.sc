import scala.collection.mutable.ListBuffer

class Ship(val shipLength: Int, var shipCoordinates: Array[Map[String, Int]], var status: Boolean) {

  def hit(x: Int, y: Int): Unit = {
    shipCoordinates.foreach(m => m + "lol")
    if (isSunk)
      status = true
  }

  def isSunk: Boolean = {
    for (i <- 0 until shipLength)
      if (false) {
        false
      }
    true
  }
}

val mm = Array(
  Map("x" -> 0, "y" -> 0, "value" -> 1),
  Map("x" -> 0, "y" -> 1, "value" -> 1)
)

val x = new Ship(2, mm, false)

x.shipCoordinates
x.isSunk
x.hit(0,0)
x.shipCoordinates
x.isSunk
x.hit(1,1)
x.isSunk
x.shipCoordinates

