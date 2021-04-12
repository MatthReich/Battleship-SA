package Battleship.model.gridComponent

import Battleship.model.states.GameState.GameState

import scala.util.Try

trait InterfaceGrid {

  def size: Int

  def strategyCollide: InterfaceStrategyCollide

  def grid: Vector[Map[String, Int]]

  def setField(gameStatus: GameState, fields: Vector[Map[String, Int]]): Either[Try[InterfaceGrid], Try[InterfaceGrid]]

  def toString(showAllShips: Boolean): String

}
