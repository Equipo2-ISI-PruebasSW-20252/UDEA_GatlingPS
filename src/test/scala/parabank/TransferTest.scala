package parabank

import scala.concurrent.duration.DurationInt
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import parabank.Data._

class TransferTest extends Simulation{

  // 0 Define feeder
  val feeder = csv("transaction.csv").circular

  // 1 Http Conf
  val httpConf = http
    .acceptHeader("*/*")

  // 2 Scenario Definition
  val scn = scenario("Transactions")
    .feed(feeder)
    .exec(http("Login USER Request")
      .get(s"$url/parabank/services/bank/login/$username/$password")
      .check(status.is(200))
    ).pause(1.second)
    .exec { session =>
      val accountId = session("accountId").as[String]
      val amount = session("amount").as[String]
      val fullUrl = s"$url/parabank/services/bank/deposit?accountId=$accountId&amount=$amount"
      println(s"=== FULL URL: $fullUrl ===")
      session
    }
    .exec(http("Deposits funds request")
      .post(s"$url/parabank/services/bank/deposit?accountId=#{accountId}&amount=#{amount}")
      .check(status.is(200))
      .check(regex("Successfully deposited").exists)
    ).pause(1.second)

  // 4 Load Scenario
  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)
}