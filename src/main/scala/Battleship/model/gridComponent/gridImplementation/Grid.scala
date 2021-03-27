package Battleship.model.gridComponent.gridImplementation

import Battleship.controller.controllerbaseimpl.GameState
import Battleship.controller.controllerbaseimpl.GameState.GameState
import Battleship.model.gridComponent.{InterfaceGrid, InterfaceStrategyCollide}

import scala.collection.mutable

case class Grid(size: Int, strategyCollide: InterfaceStrategyCollide) extends InterfaceGrid {

  override def setField(gameStatus: GameState, fields: Array[mutable.Map[String, Int]]): (InterfaceGrid, Boolean) = {
    if (gameStatus == GameState.SHIPSETTING) {
      if (!strategyCollide.collide(fields, grid)) {
        fields.foreach(coordsfield => grid.foreach(coordsgrid => if (coordsgrid.get("x").contains(coordsfield.get("x")) && coordsgrid.get("y").contains(coordsfield.get("y"))) {
          coordsgrid("value") = 1
        }))
        return (this, true)
      }
      return (this, false)
    } else {
      grid.foreach(coords => if (coords.get("x").contains(fields(0).get("x")) && coords.get("y").contains(fields(0).get("y")))
        if (coords.get("value").contains(0)) {
          coords("value") = 2
          return (this, true)
        } else if (coords.get("value").contains(1)) {
          coords("value") = 3
          return (this, true)
        }
      )
      return (this, false)
    }
  }

  override def grid: Array[mutable.Map[String, Int]] = initgrid()

  private def initgrid(): Array[mutable.Map[String, Int]] = {
    var tmpArray = new Array[mutable.Map[String, Int]](size * size)
    for (i <- 0 until size * size) {
      tmpArray(i) = mutable.Map("x" -> i % size, "y" -> i / size, "value" -> 0)
    }
    return tmpArray
  }

  // setField (Feld setzen, überprüfen ob feld schonmal beschossen wurde) new (Grid,ischanged)

  // setShip (Felder setzen, überprüfen ob felder valide sind) new (Grid, ischanged)

  /*
  0=water
  1=ship
  2=hitwater
  3=hitship
   */
}
