package parabank

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import parabank.Data._

class PaymentServiceTest extends Simulation {

    // 1 Http conf
    val httpConf = http.baseUrl(url)
        .acceptHeader("application/json")
        .check(status.is(200))

    // 2 Define feeder
    val paymentsFeeder = csv("data/payments.csv").circular

    // 3 Scenario Definition
    val scn = scenario("PaymentService")
        .feed(paymentsFeeder)
        .exec(http("PaymentService")
            .post("/billpay")
            .queryParam("accountId", "${accountId}")
            .queryParam("amount", "${amount}")
            .check(status.is(200))
        )

    // 4 Load Scenario
    setUp(
        scn.inject(
            atOnceUsers(200)
        ).protocols(httpConf)
    ).assertions(
        global.responseTime.mean.lte(3000),
        global.successfulRequests.percent.gte(99)
    )
}