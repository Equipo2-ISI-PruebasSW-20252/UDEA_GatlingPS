package parabank

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import parabank.Data._

class CargaSimultaneaEstadoCuenta extends Simulation{

  // 1 Http Conf
  val httpConf = http.baseUrl(url)
    .acceptHeader("application/json")
    .check(status.is(200))

  // 2 Define feeder
  val accountFeeder = csv("data/cargaSimultaneaEstadoCuenta.csv").circular

  // 3 Scenario Definition
  val scn = scenario("ConsultaEstadoCuenta")
    .feed(accountFeeder)
    .exec(http("ConsultaEstadoCuenta")
      .get("/accounts/${accountId}")
      .check(status.is(200))
      .check(responseTimeInMillis.lte(3000))
    )

  // 4 Load Scenario
  setUp(
    scn.inject(
      //atOnceUsers(200) // 200 usuarios simultaneos al inicio
      constantConcurrentUsers(200).during(30.seconds) // 200 usuarios concurrentes durante 30 segundos
    ).protocols(httpConf)
  ).assertions(
    global.responseTime.max.lte(3000),
    global.successfulRequests.percent.gte(99)
  )
}
