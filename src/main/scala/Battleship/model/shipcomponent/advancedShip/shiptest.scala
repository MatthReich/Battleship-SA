package Battleship.model.shipcomponent.advancedShip

object shiptest {
  def main(args: Array[String]): Unit = {
    val mm = Array(
      scala.collection.mutable.Map("x" -> 0, "y" -> 0, "value" -> 1),
      scala.collection.mutable.Map("x" -> 0, "y" -> 1, "value" -> 1)
    )

    val x = new Ship(2, mm, false)

    println(x.shipCoordinates.mkString("Array(", ", ", ")"))
    println(x.isSunk)
    println(x.hit(0,0))
    println(x.shipCoordinates.mkString("Array(", ", ", ")"))
    println(x.isSunk)
    println(x.hit(0,2))
    println(x.isSunk)
    println(x.shipCoordinates.mkString("Array(", ", ", ")"))
  }
}
