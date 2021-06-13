package Battleship.model.gridComponent.stratgeyCollideImplementation

import Battleship.model.gridComponent.StrategyCollideInterface

import scala.annotation.tailrec

case class StrategyCollideNormal() extends StrategyCollideInterface:

    override def collides(
        fields: Vector[Map[String, Int]],
        grid: Vector[Map[String, Int]]): Either[Vector[Int], Vector[Int]] =
        val indexes = calculateIndexesRec(0, fields.length, fields, grid, Vector[Int]())

        if indexes.nonEmpty && !indexes.exists(_.equals(-1)) then
            indexes.foreach(index => if grid(index).getOrElse("value", 0) != 0 then return Left(indexes))
        Right(indexes)

    @tailrec
    private def calculateIndexesRec(
        start: Int,
        end: Int,
        fields: Vector[Map[String, Int]],
        grid: Vector[Map[String, Int]],
        result: Vector[Int]): Vector[Int] =
        if start == end then result
        else
            val index    = grid.indexWhere(
                mapping =>
                    mapping.get("x").contains(fields(start).getOrElse("x", Int.MaxValue)) && mapping.get("y").contains(
                        fields(start).getOrElse("y", Int.MaxValue)))
            val newStart = start + 1
            if index == -1 then calculateIndexesRec(newStart, end, fields, grid, result)
            else calculateIndexesRec(newStart, end, fields, grid, result.appended(index))
            end if
