package parabank

import scala.concurrent.duration.DurationInt
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import parabank.Data._

class TransferTest extends Simulation{

  // 0 Define feeder
  val feeder = csv("transaction.csv").circular

  // 1 Http Conf - Habilitar cookies
  val httpConf = http
    .acceptHeader("*/*")
    .shareConnections // Compartir conexiones para mantener sesión

  // 2 Scenario Definition
  val scn = scenario("Transactions")
    .feed(feeder)
    .exec(http("Login USER Request")
      .get(s"$url/parabank/services/bank/login/$username/$password")
      .check(status.is(200))
      // No necesitamos guardar cookies explícitamente, Gatling lo hace automáticamente
    ).pause(1.second)
    .exec(http("Deposits funds request")
      .post(s"$url/parabank/services/bank/deposit?accountId=#{accountId}&amount=#{amount}")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .body(StringBody(""))
      .check(status.is(200))
      .check(regex("Successfully deposited").exists)
    ).pause(1.second)

  // 4 Load Scenario
  setUp(
    scn.inject(constantUsersPerSec(150) during(30.seconds))
  ).protocols(httpConf)
    .assertions(
      global.successfulRequests.percent.is(99)
    )
}