package Battleship.model.gridComponent

import Battleship.config.GameModule
import Battleship.model.gridComponent.gridImplementation.Grid
import Battleship.model.shipComponent.shipImplemenation.Ship
import com.google.inject.Guice
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.mutable.ListBuffer

class StrategyCollideNormalSpec extends AnyWordSpec {
  
  "A collision" when {


    "a Ship is set at a position where is not a ship is set" should {

      "return true" in {
      }
    }

    "a Ship is set at a position where is already a ship is set" should {

      "return false" in {
      }
    }
  }
}
