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
    .disableFollowRedirect

  // 2 Scenario Definition
  val scn = scenario("Transactions")
  .feed(feeder)
    .exec(http("Login USER Request")
      .get(s"/parabank/services/bank/login/$username/$password")
      .check(status.is(200))
    ).pause(1.second)

    .exec { session =>
      println(s"AccountId: ${session("accountId").as[String]}, Amount: ${session("amount").as[String]}")
      session
    }

    .exec(http("Deposits funds request")
      .post("/parabank/services/bank/deposit")
      .queryParam("accountId", "#{accountId}")
      .queryParam("amount", "#{amount}")
      .header("accept", "application/xml")
      .body(StringBody(""))

      .check(status.is(200))
      .check(regex("Successfully deposited").exists)
    ).pause(1.second)

  // 4 Load Scenario
  setUp(
    scn.inject(atOnceUsers(1)) // Solo 1 usuario para debug
  ).protocols(httpConf)
}