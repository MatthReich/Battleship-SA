package Battleship.model.gridComponent

import Battleship.model.shipComponent.shipImplemenation.Ship

trait InterfaceStrategyCollide {

  def collide(ship: Ship, grid: InterfaceGrid): Boolean

}
