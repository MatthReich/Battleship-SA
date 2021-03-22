package Battleship.model.gridComponent.gridImplementation

import Battleship.model.gridComponent.InterfaceGrid
import Battleship.model.shipComponent.shipImplemenation.Ship

import scala.collection.mutable.ListBuffer

class Grid(val size: Int, var listOfShips: ListBuffer[Ship]) extends InterfaceGrid {
  private var field = Array.ofDim[Int](size, size)


  def addShip(): Unit = {

    update()
  }

  private def update(): Unit = {

  }

  def winStatement(): Boolean = {
    listOfShips.nonEmpty && listOfShips.exists(_.status == false)
  }
}
