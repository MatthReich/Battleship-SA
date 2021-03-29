package Battleship.model.gridComponent

import Battleship.controller.controllerComponent.GameState.GameState

import scala.collection.mutable

trait InterfaceGrid {

  def size: Int

  def strategyCollide: InterfaceStrategyCollide

  def grid: Array[mutable.Map[String, Int]]

  def setField(gameStatus: GameState, fields: Array[mutable.Map[String, Int]]): (InterfaceGrid, Boolean)

  def toString(showAllShips: Boolean): String

}
