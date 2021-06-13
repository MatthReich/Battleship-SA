package Battleship.model.gridComponent.gridImplementation

import Battleship.controller.controllerComponent.states.GameStates
import scala.util.Try
import Battleship.model.gridComponent.stratgeyCollideImplementation.StrategyCollideNormal
import Battleship.model.gridComponent.{GridInterface, StrategyCollideInterface}
import scala.annotation.tailrec
import scala.util.Failure
import scala.util.Success

case class Grid(
    val size: Int = 10,
    val strategyCollide: StrategyCollideInterface = StrategyCollideNormal(),
    val grid: Vector[Map[String, Int]] = Vector[Map[String, Int]]())
    extends GridInterface:
    private val water: Int    = 0
    private val ship: Int     = 1
    private val waterHit: Int = 2
    private val shipHit: Int  = 3

    override def setFields(gameState: GameStates, fields: Vector[Map[String, Int]]): Try[GridInterface] =
        strategyCollide.collides(fields, grid) match
            case Left(indexes)  =>
                if gameState == GameStates.SHIPSETTING then Failure(new Exception("There is already a ship placed!"))
                else updateGridIfIndexesAreRight(indexes, gameState)
                end if
            case Right(indexes) => updateGridIfIndexesAreRight(indexes, gameState)

    private def updateGridIfIndexesAreRight(indexes: Vector[Int], gameState: GameStates): Try[GridInterface] =
        if (indexes.nonEmpty && !indexes.exists(_.equals(-1))) then
            Success(updateGridRec(0, indexes.length, indexes, gameState, grid))
        else Failure(new Exception("input is out of scope"))
        end if

    @tailrec
    private def updateGridRec(
        start: Int,
        end: Int,
        indexes: Vector[Int],
        gameState: GameStates,
        result: Vector[Map[String, Int]]): GridInterface =
        if (start == end) this.copy(grid = result)
        else updateGridRec(
            start + 1,
            end,
            indexes,
            gameState,
            result.updated(indexes(start), newValueOfField(indexes(start), gameState)))

    private def newValueOfField(index: Int, gameState: GameStates): Map[String, Int] =
        grid(index).getOrElse("value", Int.MaxValue) match {
            case 0 =>
                if (gameState == GameStates.SHIPSETTING) {
                    grid(index) + ("value" -> ship)
                } else {
                    grid(index) + ("value" -> waterHit)
                }
            case 1 => grid(index) + ("value" -> shipHit)
            case _ => grid(index)
        }

    override def init() = this.copy(grid = initGridRec(0, size * size, Vector[Map[String, Int]]()))

    @tailrec
    private def initGridRec(start: Int, end: Int, result: Vector[Map[String, Int]]): Vector[Map[String, Int]] =
        if start == end then result
        else initGridRec(start + 1, end, result.appended(Map("x" -> start % size, "y" -> start / size, "value" -> 0)))
        end if
