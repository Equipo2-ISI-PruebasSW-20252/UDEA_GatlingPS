package parabank

import scala.concurrent.duration.DurationInt
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import parabank.Data._

class TransferTest extends Simulation{

  // 0 Define feeder
  val feeder = csv("transaction.csv").circular

  // 1 Http Conf - SIN baseUrl para prueba
  val httpConf = http
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate, br")
    .connectionHeader("keep-alive")

  // 2 Scenario Definition
  val scn = scenario("Transactions")
    .feed(feeder)
    .exec(http("Login USER Request")
      .get(s"$url/parabank/services/bank/login/$username/$password")
      .check(status.is(200))
    ).pause(1.second)
    .exec(http("Deposits funds request")
      .post(s"$url/parabank/services/bank/deposit")
      .queryParam("accountId", "#{accountId}")
      .queryParam("amount", "#{amount}")
      .header("Content-Length", "0")
      .check(status.is(200))
      .check(regex("Successfully deposited").exists)
    ).pause(1.second)

  // 4 Load Scenario
  setUp(
    scn.inject(atOnceUsers(1)) // Solo 1 para debug
  ).protocols(httpConf)
}