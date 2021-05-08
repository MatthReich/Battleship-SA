package Battleship.model.gridComponent.gridImplementation

import Battleship.model.gridComponent.{InterfaceGrid, InterfaceStrategyCollide}
import com.google.inject.Inject

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

case class Grid @Inject()(size: Int, strategyCollide: InterfaceStrategyCollide, grid: Vector[Map[String, Int]]) extends InterfaceGrid {

  private val water: Int = 0
  private val ship: Int = 1
  private val waterHit: Int = 2
  private val shipHit: Int = 3

  override def setField(gameState: String, fields: Vector[Map[String, Int]]): Either[Try[InterfaceGrid], Try[InterfaceGrid]] = {
    strategyCollide.collide(fields, grid) match {
      case Left(indexes) => Left(updateGridIfIndexesAreRight(indexes, gameState))
      case Right(indexes) => Right(updateGridIfIndexesAreRight(indexes, gameState))
    }
  }

  private def updateGridIfIndexesAreRight(indexes: Vector[Int], gameState: String): Try[InterfaceGrid] = {
    if (indexes.nonEmpty && !indexes.exists(_.equals(-1)))
      Success(updateGridRec(0, indexes.length, indexes, gameState, grid))
    else Failure(new Exception("input is out of scope"))
  }

  @tailrec
  private def updateGridRec(start: Int, end: Int, indexes: Vector[Int], gameState: String, result: Vector[Map[String, Int]]): InterfaceGrid = {
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

  private def newValueOfField(index: Int, gameState: String): Map[String, Int] = {
    grid(index).getOrElse("value", Int.MaxValue) match {
      case 0 => if (gameState == "SHIPSETTING") {
        grid(index) + ("value" -> ship)
      } else {
        grid(index) + ("value" -> waterHit)
      }
      case 1 => grid(index) + ("value" -> shipHit)
      case _ => grid(index)
    }
  }

}
