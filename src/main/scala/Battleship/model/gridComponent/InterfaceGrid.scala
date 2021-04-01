package Battleship.model.gridComponent

import Battleship.controller.controllerComponent.states.GameState.GameState

trait InterfaceGrid {

  def size: Int

  def strategyCollide: InterfaceStrategyCollide

  def grid: Vector[Map[String, Int]]

  def setField(gameStatus: GameState, fields: Vector[Map[String, Int]]): (InterfaceGrid, Boolean)

  def toString(showAllShips: Boolean): String

}
