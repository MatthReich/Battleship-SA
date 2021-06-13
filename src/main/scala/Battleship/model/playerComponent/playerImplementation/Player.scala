package Battleship.model.playerComponent.playerImplementation

import Battleship.model.playerComponent.PlayerInterface
import Battleship.model.shipComponent.ShipInterface
import Battleship.model.gridComponent.GridInterface
import Battleship.model.gridComponent.gridImplementation.Grid

case class Player(
    val name: String = "",
    val shipSetList: Map[String, Int] = Map("2" -> 2, "3" -> 1, "4" -> 1, "5" -> 2),
    val shipList: Vector[ShipInterface] = Vector(),
    val grid: GridInterface = Grid())
    extends PlayerInterface:
    override def updateName(newName: String): PlayerInterface = this.copy(name = newName)

    override def updateGrid(newGrid: GridInterface): PlayerInterface = this.copy(grid = newGrid)

    override def updateShipSetList(valueIn: String): PlayerInterface                  = shipSetList.get(valueIn) match
        case None        => this
        case Some(value) => this.copy(shipSetList = shipSetList.updated(valueIn, value - 1))

    override def updateShipSetList(newShipSetList: Map[String, Int]): PlayerInterface =
        this.copy(shipSetList = newShipSetList)

    override def updateShip(newShipList: Vector[ShipInterface]): PlayerInterface = this.copy(shipList = newShipList)

    override def updateShip(oldShip: ShipInterface, newShip: ShipInterface): PlayerInterface =
        this.copy(shipList = shipList.updated(shipList.indexOf(oldShip), newShip))

    override def addShip(ship: ShipInterface): PlayerInterface = this.copy(shipList = shipList.appended(ship))
