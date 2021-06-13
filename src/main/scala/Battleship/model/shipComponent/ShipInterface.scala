package Battleship.model.shipComponent

import scala.util.Try

trait ShipInterface:
    def shipLength: Int

    def shipCoordinates: Vector[Map[String, Int]]

    def status: Boolean

    def hit(x: Int, y: Int): Try[ShipInterface]
