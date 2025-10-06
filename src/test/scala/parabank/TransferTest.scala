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

  // 2 Scenario Definition - SIN LOGIN
  val scn = scenario("Transactions")
    .feed(feeder)
    .exec(http("Deposits funds request")
      .post("/parabank/services/bank/deposit?accountId=#{accountId}&amount=#{amount}")
      .header("Accept", "*/*")
      .header("Accept-Encoding", "gzip, deflate, br")
      .header("Connection", "keep-alive")
      .header("Content-Length", "0")
      .check(status.is(200))
      .check(regex("Successfully deposited").exists)
    )

  // 4 Load Scenario
  setUp(
    scn.inject(constantUsersPerSec(150) during(30.seconds))
  ).protocols(httpConf)
    .assertions(
      global.successfulRequests.percent.is(99)
    )
}