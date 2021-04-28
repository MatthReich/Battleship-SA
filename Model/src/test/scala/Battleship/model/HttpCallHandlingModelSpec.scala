package Battleship.model

import Battleship.AkkaHttpModel
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class HttpCallHandlingModelSpec extends AnyWordSpec with Matchers with ScalatestRouteTest {

  val modelRouteUnderTest: Route = AkkaHttpModel.route

  "The AkkaHttpCall" when {

    "new" should {

      "return default values for GET requests to the model optional parameter" in {

        Get("/model?getPlayerName=player_01") ~> modelRouteUnderTest ~> check {
          responseAs[String] shouldEqual "\"player_01\""
        }
        Get("/model?getPlayerName=player_02") ~> modelRouteUnderTest ~> check {
          responseAs[String] shouldEqual "\"player_02\""
        }
        Get("/model?getPlayerShipSetList=player_01") ~> modelRouteUnderTest ~> check {
          responseAs[String] shouldEqual "[[2,2],[3,1],[4,1],[5,2]]"
        }
        Get("/model?getPlayerShipSetList=player_02") ~> modelRouteUnderTest ~> check {
          responseAs[String] shouldEqual "[[2,2],[3,1],[4,1],[5,2]]"
        }
        Get("/model?getPlayerGrid=player_01") ~> modelRouteUnderTest ~> check {
          responseAs[String] shouldEqual "\"\""
        }
        Get("/model?getPlayerGrid=player_02") ~> modelRouteUnderTest ~> check {
          responseAs[String] shouldEqual "\"\""
        }
        Get("/model/player/name/update?playerName=player_01&newPlayerName=Marcel") ~> modelRouteUnderTest ~> check {
          response.status shouldEqual StatusCodes.OK
        }
        Get("/model/player/name/update?playerName=player_02&newPlayerName=Matthias") ~> modelRouteUnderTest ~> check {
          response.status shouldEqual StatusCodes.OK
        }

      }
    }

    "player names get updated" should {

      "player_01 should get updated" in {
        Get("/model/player/name/update?playerName=player_01&newPlayerName=Marcel") ~> modelRouteUnderTest ~> check {
          response.status shouldEqual StatusCodes.OK
        }
        Get("/model?getPlayerName=player_01") ~> modelRouteUnderTest ~> check {
          responseAs[String] shouldEqual "\"Marcel\""
        }
      }
      "player_02 should get updated" in {
        Get("/model/player/name/update?playerName=player_02&newPlayerName=Matthias") ~> modelRouteUnderTest ~> check {
          response.status shouldEqual StatusCodes.OK
        }
        Get("/model?getPlayerName=player_02") ~> modelRouteUnderTest ~> check {
          responseAs[String] shouldEqual "\"Matthias\""
        }
      }

    }
  }
}