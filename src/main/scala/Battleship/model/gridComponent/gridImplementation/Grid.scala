package Battleship.model.gridComponent.gridImplementation

import Battleship.controller.controllerbaseimpl.GameState
import Battleship.controller.controllerbaseimpl.GameState.GameState
import Battleship.model.gridComponent.{InterfaceGrid, InterfaceStrategyCollide}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class Grid(size: Int, strategyCollide: InterfaceStrategyCollide, grid: Array[mutable.Map[String, Int]]) extends InterfaceGrid {

  override def setField(gameStatus: GameState, fields: Array[mutable.Map[String, Int]]): (InterfaceGrid, Boolean) = {
    val retVal = strategyCollide.collide(fields, grid)
    val collide = retVal._1
    val indexes = retVal._2

    if (gameStatus == GameState.SHIPSETTING) {
      if (collide) {
        return (this, false)
      }
    }
    if (indexes.nonEmpty) {
      indexes.foreach(index => grid.update(index, newValueOfField(index, gameStatus)))
      return (this, true)
    }
    (this, false)
  }

  def initGrid(): InterfaceGrid = {
    val tmpArray = new Array[mutable.Map[String, Int]](size * size)
    for (i <- 0 until size * size) {
      tmpArray(i) = mutable.Map("x" -> i % size, "y" -> i / size, "value" -> 0)
    }
    this.copy(grid = tmpArray)
  }

  private def newValueOfField(index: Int, gameState: GameState): mutable.Map[String, Int] = {
    grid(index).getOrElse("value", Int.MaxValue) match {
      case 0 => if (gameState == GameState.SHIPSETTING) {
        grid(index) + ("value" -> 1)
      } else {
        grid(index) + ("value" -> 2)
      }
      case 1 => grid(index) + ("value" -> 3)
    }
  }

  /*
  0=water
  1=ship
  2=hitwater
  3=hitship
   */
}
