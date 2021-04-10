package Battleship.model.gridComponent

import Battleship.controller.controllerComponent.states.GameState
import Battleship.model.gridComponent.gridImplementation.Grid
import Battleship.model.gridComponent.strategyCollide.StrategyCollideNormal
import org.scalatest.wordspec.AnyWordSpec

class GridSpec extends AnyWordSpec {

  val size: Int = 10
  val STRATEGY_COLLIDE: InterfaceStrategyCollide = new StrategyCollideNormal
  val shipArray: Vector[Map[String, Int]] = Vector(
    Map("x" -> 0, "y" -> 0),
    Map("x" -> 0, "y" -> 1),
    Map("x" -> 0, "y" -> 2)
  )
  val shipArrayFalse: Vector[Map[String, Int]] = Vector(
    Map("x" -> 0, "y" -> 10),
    Map("x" -> 0, "y" -> 1),
    Map("x" -> 0, "y" -> 2)
  )

  private val IDLE: GameState.Value = GameState.IDLE
  private val SHIP_SETTING: GameState.Value = GameState.SHIPSETTING
  private val waterVector: Vector[Map[String, Int]] = Vector(Map("x" -> 5, "y" -> 5))
  private val shipVector: Vector[Map[String, Int]] = Vector(Map("x" -> 0, "y" -> 0))
  private val water: Int = 0
  private val SHIP: Int = 1
  private val WATER_HIT: Int = 2
  private val shipHit: Int = 3


  "A Grid" when {

    var grid: InterfaceGrid = Grid(size, STRATEGY_COLLIDE, null).initGrid()

    "new" should {
      "have a right grid length" in {
        assert(grid.grid.length === size * size)
      }
      "have the right mappings" in {
        assert(grid.grid.exists(_.get("value").contains(1)) === false)
      }
      "has right collide strategy" in {
        assert(grid.strategyCollide === StrategyCollideNormal())
      }
      "has right size" in {
        assert(grid.size === size)
      }
    }

    // "when set a field" should {
    //   "should not work with values out of scope" in {
    //     assert(grid.setField(IDLE, Vector(Map("x" -> 10, "y" -> 0))).isFailure)
    //   }
    // }

    //"when a field in SHIPSETTING gets changed" should {
    //  "change a water field into an ship field with success" in {
    //    grid.setField(SHIP_SETTING, shipVector) match {
    //      case Failure(_) => fail("failed to set fields but should do it")
    //      case Success(newGrid) => grid = newGrid
    //    }
    //    assert(grid.grid(0).get("value").contains(SHIP))
    //  }
    //  "not change a ship field if there is already one and return failure" in {
    //    grid.setField(SHIP_SETTING, shipVector) match {
    //      case Failure(exception) =>
    //        assert(exception.getMessage === "there is already a ship placed")
    //        assert(grid.grid(0).get("value").contains(SHIP))
    //      case Success(_) => fail("should fail but worked")
    //    }
    //  }
    //}

    //"when a field in IDLE gets changed" should {
    //  "change a water field into an water hit field" in {
    //    grid.setField(IDLE, waterVector) match {
    //      case Failure(_) => fail("failed to set fields but should do it")
    //      case Success(newGrid) => grid = newGrid
    //    }
    //    assert(grid.grid(55).get("value").contains(WATER_HIT))
    //  }
    //  "not change a water hit field in any way" in {
    //    grid.setField(IDLE, waterVector) match {
    //      case Failure(_) => fail("failed to set fields but should do it")
    //      case Success(newGrid) => grid = newGrid
    //    }
    //    assert(grid.grid(55).get("value").contains(WATER_HIT))
    //  }
    //  "change a ship field into an ship hit field" in {
    //    grid.setField(IDLE, shipVector) match {
    //      case Failure(_) => fail("failed to set fields but should do it")
    //      case Success(newGrid) => grid = newGrid
    //    }
    //    assert(grid.grid(0).get("value").contains(shipHit))
    //  }
    //  "not change a ship hit field in any way" in {
    //    grid.setField(IDLE, shipVector) match {
    //      case Failure(_) => fail("failed to set fields but should do it")
    //      case Success(newGrid) => grid = newGrid
    //    }
    //    assert(grid.grid(0).get("value").contains(shipHit))
    //  }
    //}

    // "toString" should {
    //
    //   "build a new grid" in {
    //     assert(grid.toString(true).length == 1483)
    //   }
    //   "build a grid with true where all ships can be seen" in {
    //     grid.setField(SHIP_SETTING, Vector(Map("x" -> 0, "y" -> 1))) match {
    //       case Failure(_) => fail("failed to set fields but should do it")
    //       case Success(newGrid) => grid = newGrid
    //     }
    //     assert(grid.toString(true).containsSlice(Console.GREEN + "  x  " + Console.RESET))
    //   }
    //   "build a grid with false where not the ships can be seen" in {
    //     assert(!grid.toString(false).containsSlice(Console.GREEN + "  x  " + Console.RESET))
    //
    //   }
    // }

  }
}
