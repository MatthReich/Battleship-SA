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

package battleship.gatling.model.database_single_simulation

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class DatabaseEnduranceSimulation extends Simulation {

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

  // A scenario is a chain of requests and pauses
  def val endurance = scenario("Testing Database - Endurance")
    .repeat(1000, "i") {
      exec(
        http("update databse - endurance")
          .get("/model/database?request=save")
      )
        .pause(1)
        .exec(
          http("read database - endurance")
            .get("/model/database?request=load")
        )
    }

  setUp(
    endurance.inject(atOnceUsers(1))
  ).protocols(httpProtocol)

}
