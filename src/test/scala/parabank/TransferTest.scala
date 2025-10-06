package parabank

import scala.concurrent.duration.DurationInt
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import parabank.Data._

class TransferTest extends Simulation{

  // 0 Define feeder
  val feeder = csv("transaction.csv").circular

  // 1 Http Conf
  val httpConf = http.baseUrl(url)
    .acceptHeader("application/xml")

  // 2 Scenario Definition
  val scn = scenario("Transactions")
    .feed(feeder)
    .exec(http("Login USER Request")
      .get(s"/parabank/services/bank/login/$username/$password")
      .check(status.is(200))
    )
    .pause(1.second)

    .exec(http("Deposits funds request")
      .post("/parabank/services/bank/transfer/#{fromAccountId}/#{toAccountId}/#{amount}")

      .check(status.is(200))
      .check(regex("Successfully transferred").exists)
    ).pause(1.second)

  // 4 Load Scenario
  setUp(
    scn.inject(constantUsersPerSec(150) during(30.seconds))
  ).protocols(httpConf)
    .assertions(
      global.successfulRequests.percent.is(99)
    )
}