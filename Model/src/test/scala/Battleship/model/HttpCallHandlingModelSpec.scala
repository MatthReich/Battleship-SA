package Battleship.model

import Battleship.AkkaHttpModel
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class HttpCallHandlingModelSpec extends AnyWordSpec with Matchers with ScalatestRouteTest {

  val routeUnderTest: Route = AkkaHttpModel.route

  "The AkkaHttpCall" when {

    "new" should {

      "return default values for GET requests to the model optional parameter" in {

        Get("/model?getPlayerName=player_01") ~> routeUnderTest ~> check {
          responseAs[String] shouldEqual "\"player_01\""
        }
        Get("/model?getPlayerName=player_02") ~> routeUnderTest ~> check {
          responseAs[String] shouldEqual "\"player_02\""
        }
        Get("/model?getPlayerShipSetList=player_01") ~> routeUnderTest ~> check {
          responseAs[String] shouldEqual "[[2,2],[3,1],[4,1],[5,2]]"
        }
        Get("/model?getPlayerShipSetList=player_02") ~> routeUnderTest ~> check {
          responseAs[String] shouldEqual "[[2,2],[3,1],[4,1],[5,2]]"
        }
        Get("/model/player/name/update?playerName=player_01&newPlayerName=Marcel") ~> routeUnderTest ~> check {
          response.status shouldEqual StatusCodes.OK
        }
        Get("/model/player/name/update?playerName=player_02&newPlayerName=Matthias") ~> routeUnderTest ~> check {
          response.status shouldEqual StatusCodes.OK
        }

      }

      "fail to give a grid" in {
        Get("/model?getPlayerGrid=player_01") ~> routeUnderTest ~> check {
          response.status shouldEqual StatusCodes.BadRequest
        }
        Get("/model?getPlayerGrid=player_02") ~> routeUnderTest ~> check {
          response.status shouldEqual StatusCodes.BadRequest
        }
      }

    }

    "player names get updated" should {

      "player_01 should get updated" in {
        Get("/model/player/name/update?playerName=player_01&newPlayerName=Marcel") ~> routeUnderTest ~> check {
          response.status shouldEqual StatusCodes.OK
        }
        Get("/model?getPlayerName=player_01") ~> routeUnderTest ~> check {
          responseAs[String] shouldEqual "\"Marcel\""
        }
      }
      "player_02 should get updated" in {
        Get("/model/player/name/update?playerName=player_02&newPlayerName=Matthias") ~> routeUnderTest ~> check {
          response.status shouldEqual StatusCodes.OK
        }
        Get("/model?getPlayerName=player_02") ~> routeUnderTest ~> check {
          responseAs[String] shouldEqual "\"Matthias\""
        }
      }
    }

    "false params used" should {

      "return bad request" in {
        Get("/model?getPlayerName=player_03") ~> routeUnderTest ~> check {
          response.status shouldEqual StatusCodes.BadRequest
        }
        Get("/model?getPlayerShipSetList=player_03") ~> routeUnderTest ~> check {
          response.status shouldEqual StatusCodes.BadRequest
        }
        Get("/model?getPlayerGrid=player_03") ~> routeUnderTest ~> check {
          response.status shouldEqual StatusCodes.BadRequest
        }
        Get("/model/player/name/update?playerName=player_03&newPlayerName=test") ~> routeUnderTest ~> check {
          response.status shouldEqual StatusCodes.custom(469, "")
        }
      }
    }

  }
}