package parabank

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import parabank.Data._

class LoanRequestTest extends Simulation{

  // 1 Http conf
  val httpConf = http.baseUrl(url)
    .acceptHeader("application/json")
    .check(status.is(200))

  // 2 Define feeder
  val loanFeeder = csv("data/loanRequests.csv").circular

  // 3 Scenario Definition
  val scn = scenario("LoanRequests")
    .feed(loanFeeder)
    .exec(http("LoanRequests")
      .post("/requestLoan")
      .queryParam("customerId", "${customerId}")
      .queryParam("amount", "${amount}")
      .queryParam("downPayment", "${downPayment}")
      .queryParam("fromAccountId", "${fromAccountId}")
      .check(status.is(200))
      .check(jsonPath("$.approved").exists)
      .check(jsonPath("$.approved").is("true"))
    )

  // 4 Load Scenario
  setUp(
    scn.inject(
      atOnceUsers(15) // 150 usuarios simultaneos al inicio
      //constantConcurrentUsers(150).during(60.seconds)
    ).protocols(httpConf)
  ).assertions(
    global.responseTime.mean.lte(5000), // Tiempo de respuesta promedio menor o igual a 5 segundos
    global.successfulRequests.percent.gte(98), // Al menos el 98% de las solicitudes deben ser exitosas
    global.failedRequests.count.is(0) // Ninguna request debe fallar
  )
}
