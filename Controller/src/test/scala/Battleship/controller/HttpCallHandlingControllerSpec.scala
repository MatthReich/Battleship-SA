package Battleship.controller

import Battleship.controller.controllerComponent.Controller
import org.mockito.MockitoSugar
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class HttpCallHandlingControllerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  val mockedController = new ControllerMock
  val controller = new Controller()

  "A test" when {


    "test" should {

      "work" in {
        controller.save()
      }

      "work2" in {

      }

    }
  }
}
