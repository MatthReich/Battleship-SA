package Battleship.model.gridComponent.gridImplementation

import Battleship.controller.controllerComponent.states.GameState
import Battleship.controller.controllerComponent.states.GameState.GameState
import Battleship.model.gridComponent.{InterfaceGrid, InterfaceStrategyCollide}
import com.google.inject.Inject

import scala.annotation.tailrec
import scala.collection.mutable
import scala.util.{Failure, Success, Try}

case class Grid @Inject()(size: Int, strategyCollide: InterfaceStrategyCollide, grid: Vector[Map[String, Int]]) extends InterfaceGrid {

  private val water: Int = 0
  private val ship: Int = 1
  private val waterHit: Int = 2
  private val shipHit: Int = 3

  override def setField(gameState: GameState, fields: Vector[Map[String, Int]]): Try[InterfaceGrid] = {
    strategyCollide.collide(fields, grid) match {
      case Left(indexes) =>
        if (gameState == GameState.SHIPSETTING) Failure(new Exception("there is already a ship placed"))
        else updateGridIfIndexesAreRight(indexes, gameState)
      case Right(indexes) => updateGridIfIndexesAreRight(indexes, gameState)
    }
  }

  private def updateGridIfIndexesAreRight(indexes: Vector[Int], gameState: GameState): Try[InterfaceGrid] = {
    if (indexes.nonEmpty && !indexes.exists(_.equals(-1)))
      Success(updateGridRec(0, indexes.length, indexes, gameState, grid))
    else Failure(new Exception("input is out of scope"))
  }

  @tailrec
  private def updateGridRec(start: Int, end: Int, indexes: Vector[Int], gameState: GameState, result: Vector[Map[String, Int]]): InterfaceGrid = {
    if (start == end) this.copy(grid = result)
    else updateGridRec(start + 1, end, indexes, gameState, result.updated(indexes(start), newValueOfField(indexes(start), gameState)))
  }

  def initGrid(): InterfaceGrid = {
    this.copy(grid = initGridRec(0, size * size, Vector[Map[String, Int]]()))
  }

  @tailrec
  private def initGridRec(start: Int, end: Int, result: Vector[Map[String, Int]]): Vector[Map[String, Int]] = {
    if (start == end) result
    else initGridRec(start + 1, end, result.appended(Map("x" -> start % size, "y" -> start / size, "value" -> 0)))
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
    if (idx == 0 && idy == size) {
      result.toString()
    } else if (idx == size) {
      val newY = idy + 1
      result ++= "\n"
      if (newY < size) {
        result ++= newY + " "
      }
      toStringRek(0, newY, showAllShips, result)
    } else {
      val fieldValue = grid(grid.indexWhere(mapping => mapping.get("x").contains(idx) && mapping.get("y").contains(idy))).getOrElse("value", Int.MaxValue)
      result ++= getFieldValueInString(fieldValue, showAllShips)
      toStringRek(idx + 1, idy, showAllShips, result)
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
