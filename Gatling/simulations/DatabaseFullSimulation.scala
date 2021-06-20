/*
 * Copyright 2011-2021 GatlingCorp (https://gatling.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package battleship.gatling.model

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class DatabaseFullSimulation extends Simulation {

  val httpProtocol = http
    // Here is the root for all relative URLs
    .baseUrl("http://localhost:8080")
    // Here are the common headers
    .acceptHeader(
      "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
    )
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader(
      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0"
    )
  // Object
  object Save {
    def saveRequest(name: String) = {
      exec(
        http(name + " - update database")
          .get("/model/database?request=save&gameState=PLAYERSETTING&playerState=PLAYER_ONE")
      )
    }
  }

  object Load {
    def loadRequest(name: String) = {
      exec(
        http(name + " - read database")
          .get("/model/database?request=load")
      )
    }
  }

  // A scenario is a chain of requests and pauses
  val load = scenario("Testing Database - Load")
    .exec(Save.saveRequest("load"))
    .pause(1)
    .exec(Load.loadRequest("load"))

  val spike = scenario("Testing Database - Spike")
    .exec(Load.loadRequest("spike"))

  val endurance = scenario("Testing Database - Endurance")
    .exec(Save.saveRequest("endurance"))
    .pause(1)
    .exec(Load.loadRequest("endurance"))

  val stress = scenario("Testing Database - Stress")
    .exec(Save.saveRequest("stress"))
    .exec(Load.loadRequest("stress"))

  val volume = scenario("Testing Database - Volume")
    .exec(Save.saveRequest("volume"))

  setUp(
    load // normal usage
      .inject(atOnceUsers(2))
      .andThen(
        volume // huge volume of data
          .inject(atOnceUsers(2000))
          .andThen(
            spike // suddenly huge amount of requests
              .inject(nothingFor(10.seconds), atOnceUsers(2000))
              .andThen(
                stress // find the limit by increasing
                  .inject(
                    nothingFor(10.seconds),
                    rampUsersPerSec(10).to(2000).during(3.minutes)
                  )
                  .andThen(
                    endurance // some userers for long
                      .inject(
                        nothingFor(10.seconds),
                        constantUsersPerSec(10).during(5.minute)
                      )
                  )
              )
          )
      )
  ).protocols(httpProtocol)

}
