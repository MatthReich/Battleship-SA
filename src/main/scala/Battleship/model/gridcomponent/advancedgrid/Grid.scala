package Battleship.model.gridcomponent.advancedgrid

import Battleship.model.gridcomponent.InterfaceGrid

case class Grid() extends InterfaceGrid {
  private var size: Int = 10
  private var matrix = Array.ofDim[Int](size, size)

  override def setField(x: Int, y: Int, value: Int): Unit = {
    matrix(x)(y) = value
  }

  override def getField(x: Int, y: Int): Int = {
    var tmp = matrix(x)(y)
    if (tmp == 1) {
      tmp = 0
    }
    tmp
  }

  override def getValue(x: Int, y: Int): Int = {
    matrix(x)(y)
  }

  override def getSize: Int = this.size

  override def setSize(int: Int): Unit =
    size = int

  override def winStatement(): Boolean = {
    var statement = true
    for (i <- 0 until size) {
      for (j <- 0 until size) {
        if (matrix(i)(j) == 1) statement = false
      }
    }
    statement
  }

}
