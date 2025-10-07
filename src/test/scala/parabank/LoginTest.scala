/*package parabank

import scala.concurrent.duration.DurationInt
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import parabank.Data._

class LoginTest extends Simulation{

  // 1 Http Conf
  val httpConf = http.baseUrl(url)
    .acceptHeader("application/json")
    //Verificar de forma general para todas las solicitudes
    .check(status.is(200))

  // 2 Scenario Definition
  val scn = scenario("Login").
    exec(http("Login request")
      .get(s"/login/$username/$password")
      .check(status.is(200))
      .check(responseTimeInMillis.lte(2000))
    )

  val normalLoad = scenario("Normal Load - limit of 100 concurrent users")
    .exec(scn)

  val peakLoad = scenario("Peak Load - limit of 200 concurrent users")
    .exec(scn)

  // 3 Load Scenario
  setUp(
    normalLoad.inject(rampUsers(100).during(10.seconds))
      .protocols(httpConf),

    peakLoad.inject(nothingFor(15.seconds),
      rampUsers(200).during(20.seconds))
      .protocols(httpConf)
  )
    .assertions(
      details("Login request")
      .responseTime.max.lte(2000),

      details("Login request")
      .responseTime.percentile(95).lte(2000),

      details("Login request")
      .responseTime.max.lte(5000),

      details("Login request")
        .responseTime.percentile(95).lte(5000),

      global.successfulRequests.percent.gt(95)
  )
}*/