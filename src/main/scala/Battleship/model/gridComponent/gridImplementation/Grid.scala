package Battleship.model.gridComponent.gridImplementation

import Battleship.controller.controllerComponent.states.GameState
import Battleship.controller.controllerComponent.states.GameState.GameState
import Battleship.model.gridComponent.{InterfaceGrid, InterfaceStrategyCollide}
import com.google.inject.Inject

import scala.annotation.tailrec
import scala.collection.mutable

case class Grid @Inject()(size: Int, strategyCollide: InterfaceStrategyCollide, grid: Vector[Map[String, Int]]) extends InterfaceGrid {

  private val water: Int = 0
  private val ship: Int = 1
  private val waterHit: Int = 2
  private val shipHit: Int = 3

  override def setField(gameState: GameState, fields: Vector[Map[String, Int]]): (InterfaceGrid, Boolean) = {
    if (fields.exists(mapping => mapping.getOrElse("x", Int.MaxValue) > 9 || mapping.getOrElse("y", Int.MaxValue) > 9)) return (this, false)
    strategyCollide.collide(fields, grid) match {
      case Left(indexes) =>
        if (gameState == GameState.SHIPSETTING) (this, false)
        else updateGridIfIndexesAreRight(indexes, gameState)
      case Right(indexes) => updateGridIfIndexesAreRight(indexes, gameState)
    }
  }

  private def updateGridIfIndexesAreRight(indexes: Vector[Int], gameState: GameState): (InterfaceGrid, Boolean) = {
    if (indexes.nonEmpty && !indexes.exists(_.equals(-1))) {
      (updateGridRec(0, indexes.length, indexes, gameState, grid), true)
    } else (this, false)
  }

  @tailrec
  private def updateGridRec(start: Int, end: Int, indexes: Vector[Int], gameState: GameState, result: Vector[Map[String, Int]]): InterfaceGrid = {
    if (start == end) this.copy(grid = result)
    else {
      val newStart = start + 1
      updateGridRec(newStart, end, indexes, gameState, result.updated(indexes(start), newValueOfField(indexes(start), gameState)))
    }
  }

  def initGrid(): InterfaceGrid = {
    this.copy(grid = initGridRec(0, size * size, Vector[Map[String, Int]]()))
  }

  @tailrec
  private def initGridRec(start: Int, end: Int, result: Vector[Map[String, Int]]): Vector[Map[String, Int]] = {
    if (start == end) result
    else {
      val newStart = start + 1
      initGridRec(newStart, end, result.appended(Map("x" -> start % size, "y" -> start / size, "value" -> 0)))
    }
  }

  private def newValueOfField(index: Int, gameState: GameState): Map[String, Int] = {
    grid(index).getOrElse("value", Int.MaxValue) match {
      case 0 => if (gameState == GameState.SHIPSETTING) {
        grid(index) + ("value" -> ship)
      } else {
        grid(index) + ("value" -> waterHit)
      }
      case 1 => grid(index) + ("value" -> shipHit)
      case _ => grid(index)
    }
  }

  override def toString(showAllShips: Boolean): String = toStringRek(0, 0, showAllShips, initRek())

  @tailrec
  private def toStringRek(idx: Int, idy: Int, showAllShips: Boolean, result: mutable.StringBuilder): String = {
    if (idx == 0 && idy == 10) {
      result.toString()
    } else if (idx == 10) {
      val newX = 0
      val newY = idy + 1
      result ++= "\n"
      if (newY <= 9) {
        result ++= newY + " "
      }
      toStringRek(newX, newY, showAllShips, result)
    } else {
      val fieldValue = grid(grid.indexWhere(mapping => mapping.get("x").contains(idx) && mapping.get("y").contains(idy))).getOrElse("value", Int.MaxValue)
      result ++= getFieldValueInString(fieldValue, showAllShips)
      val newX = idx + 1
      toStringRek(newX, idy, showAllShips, result)
    }
  }

  private def getFieldValueInString(fieldValue: Int, showAllShips: Boolean): String = {
    fieldValue match {
      case this.water => Console.BLUE + "  ~  " + Console.RESET
      case this.ship =>
        if (showAllShips) Console.GREEN + "  x  " + Console.RESET
        else Console.BLUE + "  ~  " + Console.RESET
      case this.shipHit => Console.RED + "  x  " + Console.RESET
      case this.waterHit => Console.BLUE + "  0  " + Console.RESET
    }
  }

  private def initRek(): mutable.StringBuilder = {
    val stringOfGrid = new mutable.StringBuilder("  ")
    var ids = 0
    while (ids < size) {
      stringOfGrid ++= "  " + ids + "  "
      ids += 1
    }
    stringOfGrid ++= "\n0 "
    stringOfGrid
  }

}
