package Battleship.model.playerComponent

import Battleship.model.shipComponent.ShipInterface
import Battleship.model.gridComponent.GridInterface

trait PlayerInterface:
    def name: String

    def updateName(newName: String): PlayerInterface

    def shipSetList: Map[String, Int]

    def shipList: Vector[ShipInterface]

    def grid: GridInterface

    def updateGrid(grid: GridInterface): PlayerInterface

    def updateShipSetList(newShipSetList: Map[String, Int]): PlayerInterface

    def updateShipSetList(valueIn: String): PlayerInterface

    def updateShip(newShipList: Vector[ShipInterface]): PlayerInterface

    def updateShip(oldShip: ShipInterface, newShip: ShipInterface): PlayerInterface

    def addShip(ship: ShipInterface): PlayerInterface
