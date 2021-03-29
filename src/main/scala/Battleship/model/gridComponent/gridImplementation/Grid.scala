package Battleship.model.gridComponent.gridImplementation

import Battleship.controller.controllerComponent.GameState
import Battleship.controller.controllerComponent.GameState.GameState
import Battleship.model.gridComponent.{InterfaceGrid, InterfaceStrategyCollide}

import scala.collection.mutable

case class Grid(size: Int, strategyCollide: InterfaceStrategyCollide, grid: Array[mutable.Map[String, Int]]) extends InterfaceGrid {

  private val water: Int = 0
  private val ship: Int = 1
  private val waterHit: Int = 2
  private val shipHit: Int = 3

  override def setField(gameStatus: GameState, fields: Array[mutable.Map[String, Int]]): (InterfaceGrid, Boolean) = {
    val retVal = strategyCollide.collide(fields, grid)
    val collide = retVal._1
    val indexes = retVal._2

    if (gameStatus == GameState.SHIPSETTING) {
      if (collide) {
        return (this, false)
      }
    }
    if (indexes.nonEmpty && !indexes.exists(_.equals(-1))) {
      indexes.foreach(index => grid.update(index, newValueOfField(index, gameStatus)))
      return (this, true)
    }
    (this, false)
  }

  def initGrid(): InterfaceGrid = {
    val tmpArray = new Array[mutable.Map[String, Int]](size * size)
    for (i <- 0 until size * size) {
      tmpArray(i) = mutable.Map("x" -> i % size, "y" -> i / size, "value" -> water)
    }
    this.copy(grid = tmpArray)
  }

  private def newValueOfField(index: Int, gameState: GameState): mutable.Map[String, Int] = {
    grid(index).getOrElse("value", Int.MaxValue) match {
      case 0 => if (gameState == GameState.SHIPSETTING) {
        grid(index) + ("value" -> ship)
      } else {
        grid(index) + ("value" -> waterHit)
      }
      case 1 => grid(index) + ("value" -> shipHit)
    }
  }

}
