package Battleship.model.gridComponent.strategyCollide

import Battleship.model.gridComponent.gridImplementation.Grid
import Battleship.model.gridComponent.{InterfaceGrid, InterfaceStrategyCollide}
import Battleship.model.shipComponent.shipImplemenation.Ship

import scala.collection.mutable.ListBuffer

class StrategyCollideNormal  {

   def collide(ship: Ship, grid: Grid): Boolean = {

    grid.listOfShips.exists(_.shipCoordinates.flatMap(_.keySet) sameElements ship.shipCoordinates.flatMap(_.keySet))

  }
}
