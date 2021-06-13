package Battleship.aview.tui

import scala.annotation.tailrec
import scala.collection.mutable.StringBuilder

case class ToStringHelper(size: Int):
    private val water: Int    = 0
    private val ship: Int     = 1
    private val waterHit: Int = 2
    private val shipHit: Int  = 3

    def shipSetListToString(shipSetList: Map[String, Int]): String =
        val field = new StringBuilder()
        shipSetList.foreach(field.append(_).append("\n"))
        field.toString

    def gridToString(showAllShips: Boolean, grid: Vector[Map[String, Int]]): String =
        toStringRek(0, 0, showAllShips, grid, initRek())

    @tailrec
    private def toStringRek(
        idx: Int,
        idy: Int,
        showAllShips: Boolean,
        grid: Vector[Map[String, Int]],
        result: StringBuilder): String =
        if idx == 0 && idy == size then
            result.toString()
        else if idx == size then
            val newY = idy + 1
            result ++= "\n"
            if newY < size then result ++= newY.toString + " "
            toStringRek(0, newY, showAllShips, grid, result)
        else
            val fieldValue = grid(grid.indexWhere(
                mapping =>
                    mapping.get("x").contains(idx) && mapping.get("y").contains(idy))).getOrElse("value", Int.MaxValue)
            result ++= getFieldValueInString(fieldValue, showAllShips)
            toStringRek(idx + 1, idy, showAllShips, grid, result)
        end if

    private def getFieldValueInString(fieldValue: Int, showAllShips: Boolean): String =
        fieldValue match
            case this.water    => Console.BLUE + "  ~  " + Console.RESET
            case this.ship     =>
                if showAllShips then Console.GREEN + "  x  " + Console.RESET
                else Console.BLUE + "  ~  " + Console.RESET
                end if
            case this.shipHit  => Console.RED + "  x  " + Console.RESET
            case this.waterHit => Console.BLUE + "  0  " + Console.RESET

    private def initRek(): StringBuilder =
        val stringOfGrid = new StringBuilder("  ")
        var ids          = 0
        while ids < size do
            stringOfGrid ++= "  " + ids + "  "
            ids += 1
        stringOfGrid ++= "\n0 "
        stringOfGrid
