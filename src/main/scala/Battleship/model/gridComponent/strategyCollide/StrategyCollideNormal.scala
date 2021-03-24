package Battleship.model.gridComponent.strategyCollide

import Battleship.model.gridComponent.InterfaceStrategyCollide
import Battleship.model.gridComponent.gridImplementation.Grid
import Battleship.model.shipComponent.shipImplemenation.Ship

class StrategyCollideNormal extends InterfaceStrategyCollide {

  def collide(ship: Ship, grid: Grid): Boolean = {

    grid.listOfShips.exists(_.shipCoordinates.flatMap(_.keySet) sameElements ship.shipCoordinates.flatMap(_.keySet))

  }
}
