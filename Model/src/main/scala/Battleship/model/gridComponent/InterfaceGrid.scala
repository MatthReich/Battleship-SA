package Battleship.model.gridComponent

import scala.util.Try

trait InterfaceGrid {

  def size: Int

  def strategyCollide: InterfaceStrategyCollide

  def grid: Vector[Map[String, Int]]

  def setField(gameStatus: String, fields: Vector[Map[String, Int]]): Either[Try[InterfaceGrid], Try[InterfaceGrid]]

  def toString(showAllShips: Boolean): String

}
