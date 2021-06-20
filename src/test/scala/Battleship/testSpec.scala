package Battleship

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class testSpec extends AnyWordSpec with Matchers{
  "A Ship" when {


    "new" should {
      "testMethod" in {
        val test:Test = new Test()
        test.testMethod()
      }
    }
  }
}
