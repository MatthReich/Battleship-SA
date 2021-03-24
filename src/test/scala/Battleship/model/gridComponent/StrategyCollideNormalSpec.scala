package Battleship.model.gridComponent

import Battleship.config.GameModule
import Battleship.model.gridComponent.gridImplementation.Grid
import Battleship.model.shipComponent.shipImplemenation.Ship
import com.google.inject.Guice
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.mutable.ListBuffer

class StrategyCollideNormalSpec extends AnyWordSpec {

  val gridSize = 10
  val listOfShips = new ListBuffer[Ship]
  val shipLength = 3
  val shipArray: Array[scala.collection.mutable.Map[String, Int]] = Array(
    scala.collection.mutable.Map("x" -> 0, "y" -> 0, "value" -> 1),
    scala.collection.mutable.Map("x" -> 0, "y" -> 1, "value" -> 1),
    scala.collection.mutable.Map("x" -> 0, "y" -> 2, "value" -> 1)
  )
  val shipArrayToCollide: Array[scala.collection.mutable.Map[String, Int]] = Array(
    scala.collection.mutable.Map("x" -> 0, "y" -> 0, "value" -> 1),
    scala.collection.mutable.Map("x" -> 0, "y" -> 1, "value" -> 1),
    scala.collection.mutable.Map("x" -> 0, "y" -> 2, "value" -> 1)
  )
  val shipArrayToNotCollide: Array[scala.collection.mutable.Map[String, Int]] = Array(
    scala.collection.mutable.Map("x" -> 5, "y" -> 5, "value" -> 1),
    scala.collection.mutable.Map("x" -> 5, "y" -> 6, "value" -> 1),
    scala.collection.mutable.Map("x" -> 5, "y" -> 7, "value" -> 1)
  )
  val status = false
  val ship: Ship = new Ship(shipLength, shipArray, status)
  listOfShips.addOne(ship)
  val injector = Guice.createInjector(new GameModule)



  "A collision" when {

    val strategyCollide: InterfaceStrategyCollide = injector.getInstance(classOf[InterfaceStrategyCollide])
    val grid = new Grid(gridSize, listOfShips, strategyCollide)

    "a Ship is set at a position where is not a ship is set" should {
      val shipToNotGetCollide: Ship = new Ship(shipLength, shipArrayToNotCollide, status)

      "return true" in {
        assert(strategyCollide.collide(shipToNotGetCollide, grid) === true)
      }
    }

    "a Ship is set at a position where is already a ship is set" should {
      val shipToGetCollide: Ship = new Ship(shipLength, shipArrayToCollide, status)

      "return false" in {
        assert(strategyCollide.collide(shipToGetCollide, grid) === false)
      }
    }
  }
}
