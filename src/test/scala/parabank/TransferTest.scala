package parabank

import scala.concurrent.duration.DurationInt
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import parabank.Data._

class TransferTest extends Simulation{

  // 0 Define feeder
  val feeder = csv("transaction.csv").circular

  // 1 Http Conf - Configurar para seguir redirects como Postman
  val httpConf = http.baseUrl(url)
    .acceptHeader("application/json")
    .acceptEncodingHeader("gzip, deflate, br")
    .connectionHeader("keep-alive")
    // Por defecto Gatling sigue redirects, pero lo hacemos explícito

  // 2 Scenario Definition - SIN LOGIN
  val scn = scenario("Transactions")
    .feed(feeder)
    .exec(http("Deposits funds request")
      .post("/parabank/services/bank/deposit")
      .queryParam("accountId", "#{accountId}") // Usar queryParam en lugar de URL manual
      .queryParam("amount", "#{amount}")
      .check(status.in(200, 201, 301, 302)) // Aceptar redirects también
    )

  // 4 Load Scenario
  setUp(
    scn.inject(atOnceUsers(1)) // Solo 1 para debug
  ).protocols(httpConf)
}