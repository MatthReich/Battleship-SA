package Battleship.model.gridComponent.strategyCollide

import Battleship.model.gridComponent.InterfaceStrategyCollide

import scala.collection.mutable

case class StrategyCollideNormal() extends InterfaceStrategyCollide {
  override def collide(fields: Array[mutable.Map[String, Int]], grid: Array[mutable.Map[String, Int]]): Boolean = true
}
