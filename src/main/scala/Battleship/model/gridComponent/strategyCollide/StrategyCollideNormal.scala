package Battleship.model.gridComponent.strategyCollide

import Battleship.model.gridComponent.InterfaceStrategyCollide

import scala.annotation.tailrec

case class StrategyCollideNormal() extends InterfaceStrategyCollide {

  override def collide(fields: Vector[Map[String, Int]], grid: Vector[Map[String, Int]]): Either[Vector[Int], Vector[Int]] = {
    val indexes = calculateIndexesRec(0, fields.length, fields, grid, Vector[Int]())

    if (indexes.nonEmpty && !indexes.exists(_.equals(-1))) {
      indexes.foreach(index => if (grid(index).getOrElse("value", 0) != 0) {
        return Left(indexes)
      })
    }
    Right(indexes)
  }

  @tailrec
  private def calculateIndexesRec(start: Int, end: Int, fields: Vector[Map[String, Int]], grid: Vector[Map[String, Int]], result: Vector[Int]): Vector[Int] = {
    if (start == end) result
    else {
      val index = grid.indexWhere(mapping => mapping.get("x").contains(fields(start).getOrElse("x", Int.MaxValue)) && mapping.get("y").contains(fields(start).getOrElse("y", Int.MaxValue)))
      val newStart = start + 1
      if (index == -1) calculateIndexesRec(newStart, end, fields, grid, result)
      else calculateIndexesRec(newStart, end, fields, grid, result.appended(index))
    }
  }

}
