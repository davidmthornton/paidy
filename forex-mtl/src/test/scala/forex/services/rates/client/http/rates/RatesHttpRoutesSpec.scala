package forex.services.rates.client.http.rates

import cats.effect.IO
import forex.http.jsonDecoder
import forex.http.rates.RatesHttpRoutes
import forex.programs.RatesProgram
import forex.services.rates.client.http.rates.mocks.Mocks.MockRatesProgram
import io.circe.Json
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.{Method, Request, Response, Status}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class RatesHttpRoutesSpec extends AnyWordSpec with Matchers {

  implicit val ratesProgram: RatesProgram[IO] = new MockRatesProgram
  val httpRoutes = new RatesHttpRoutes[IO](ratesProgram)

  "RatesHttpRoutes" should {
    "respond with an error" when {
      "a provided currency is invalid" in {
        val invalidRequest: Request[IO] = Request[IO](Method.GET, uri"/rates?from=USD&to=123")
        val expectedInvalidResponse: Json = Json.obj("error" -> Json.fromString("Invalid currency: 123"))

        val response = httpRoutes.routes.run(invalidRequest).value.unsafeRunSync().getOrElse(Response.notFound)
        response.status shouldEqual Status.BadRequest
        response.as[Json].unsafeRunSync() shouldEqual expectedInvalidResponse
      }

      "both provided currencies are the same" in {
        val invalidRequest: Request[IO] = Request[IO](Method.GET, uri"/rates?from=GBP&to=GBP")
        val expectedInvalidResponse: Json = Json.obj("error" -> Json.fromString("Cannot convert to and from the same currency. Invalid conversion requested: GBP to GBP"))

        val response = httpRoutes.routes.run(invalidRequest).value.unsafeRunSync().getOrElse(Response.notFound)
        response.status shouldEqual Status.BadRequest
        response.as[Json].unsafeRunSync() shouldEqual expectedInvalidResponse
      }
      "respond with 404" when {
        "there are no provided currencies" in {
          val invalidRequest: Request[IO] = Request[IO](Method.GET, uri"/rates")

          val response = httpRoutes.routes.run(invalidRequest).value.unsafeRunSync().getOrElse(Response.notFound)
          response.status shouldEqual Status.NotFound
        }
      }
    }
  }
}
