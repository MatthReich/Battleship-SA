package Battleship.model.gridComponent

import Battleship.model.gridComponent.gridImplementation.Grid
import Battleship.model.shipComponent.shipImplemenation.Ship

trait InterfaceStrategyCollide {

  def collide(ship: Ship, grid: Grid): Boolean

}
