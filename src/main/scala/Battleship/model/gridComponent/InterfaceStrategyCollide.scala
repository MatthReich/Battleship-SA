package Battleship.model.gridComponent

import scala.collection.mutable

trait InterfaceStrategyCollide {

  def collide(fields: Array[mutable.Map[String, Int]], grid: Array[mutable.Map[String, Int]]): Boolean

}
