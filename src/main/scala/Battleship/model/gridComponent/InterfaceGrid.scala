package Battleship.model.gridComponent

import Battleship.controller.controllerComponent.states.GameState.GameState

import scala.util.Try

trait InterfaceGrid {

  def size: Int

  def strategyCollide: InterfaceStrategyCollide

  def grid: Vector[Map[String, Int]]

  def setField(gameStatus: GameState, fields: Vector[Map[String, Int]]): Try[InterfaceGrid]

  def toString(showAllShips: Boolean): String

}
