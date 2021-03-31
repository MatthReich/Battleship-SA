package Battleship.model.gridComponent.strategyCollide

import Battleship.model.gridComponent.InterfaceStrategyCollide

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class StrategyCollideNormal() extends InterfaceStrategyCollide {
  override def collide(fields: Array[mutable.Map[String, Int]], grid: Array[mutable.Map[String, Int]]): (Boolean, ListBuffer[Int]) = {
    val indexes = new ListBuffer[Int]
    fields.foreach(coords => indexes.addOne(grid.indexWhere(mapping => mapping.get("x").contains(coords.getOrElse("x", Int.MaxValue)) &&
      mapping.get("y").contains(coords.getOrElse("y", Int.MaxValue)))))
    if (indexes.nonEmpty && !indexes.exists(_.equals(-1))) {
      indexes.foreach(index => if (grid(index).getOrElse("value", 0) != 0) {
        return (true, indexes)
      })
    }
    (false, indexes)
  }
}
