package Battleship.model.gridComponent.gridImplementation

import Battleship.controller.controllerComponent.states.GameState
import Battleship.controller.controllerComponent.states.GameState.GameState
import Battleship.model.gridComponent.{InterfaceGrid, InterfaceStrategyCollide}
import com.google.inject.Inject

import scala.annotation.tailrec
import scala.collection.mutable

case class Grid @Inject()(size: Int, strategyCollide: InterfaceStrategyCollide, grid: Array[mutable.Map[String, Int]]) extends InterfaceGrid {

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
      case _ => grid(index)
    }
  }

  override def toString(showAllShips: Boolean): String = {
    val stringOfGrid: mutable.StringBuilder = initRek()
    toStringRek(0, 0, showAllShips, stringOfGrid)
  }

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
      val tmp = grid(grid.indexWhere(mapping => mapping.get("x").contains(idx) && mapping.get("y").contains(idy))).getOrElse("value", "holy shit ist das verbuggt")
      tmp match {
        case this.water => result ++= Console.BLUE + "  ~  " + Console.RESET
        case this.ship =>
          if (showAllShips) {
            result ++= Console.GREEN + "  x  " + Console.RESET
          } else {
            result ++= Console.BLUE + "  ~  " + Console.RESET
          }
        case this.shipHit => result ++= Console.RED + "  x  " + Console.RESET
        case this.waterHit => result ++= Console.BLUE + "  0  " + Console.RESET
      }
      val newX = idx + 1
      toStringRek(newX, idy, showAllShips, result)
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
