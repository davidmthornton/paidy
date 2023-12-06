package forex.services.rates.client

import cats.effect.{IO, Timer}
import forex.config.{HttpConfig, OneFrameConfig}
import forex.domain.{Currency, Price, Rate, Timestamp}
import forex.http.rates.Protocol.OneFrameApiResponse
import forex.services.rates.Errors.Error.OneFrameLookupFailed
import io.circe.Json
import io.circe.parser._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.client.Client
import org.http4s.dsl.Http4sDsl
import org.http4s.{Header, HttpApp, Response}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.typelevel.ci.CIString

import java.time.OffsetDateTime
import scala.concurrent.duration.DurationInt

class RatesClientSpec extends AnyWordSpec with Matchers with Http4sDsl[IO] {
  implicit val timer: Timer[IO] = IO.timer(scala.concurrent.ExecutionContext.Implicits.global)

  object CurrencyQueryString extends QueryParamDecoderMatcher[String]("pair")

  val mockHttpClient: Client[IO] = Client.fromHttpApp(HttpApp[IO] {
    case GET -> Root / "rates" :? CurrencyQueryString(pair) =>
      if (pair == "GBPJPY") {
        val body = parse("""[{"from":"GBP","to":"JPY","bid":0.5493447192669441,"ask":0.3908646611051285,"price":0.4701046901860363,"time_stamp":"2023-11-04T17:20:21.188Z"}]""").getOrElse(Json.fromString(""))
        IO(Response[IO](Ok).withEntity(body).withHeaders(Header.Raw(CIString("Content-Type"), "application/json")))
      } else {
        IO(Response[IO](BadRequest).withHeaders(Header.Raw(CIString("Content-Type"), "application/json")))
      }
  })

  val oneFrameConfig: OneFrameConfig = OneFrameConfig(HttpConfig("localhost", 8080, 40.seconds), "dummy-token")

  "OneFrameClient" should {
    "return a list of rates when the HTTP request to OneFrame is successful" in {
      val client = OneFrameClient.make[IO](mockHttpClient, oneFrameConfig)
      val pair = Rate.Pair(Currency.GBP, Currency.JPY)
      val result = client.getRate(pair).unsafeRunSync()

      val expectedRate = Rate(Rate.Pair(Currency.GBP, Currency.JPY), Price(BigDecimal(0.4701046901860363)), Timestamp(OffsetDateTime.parse("2023-11-04T17:20:21.188Z")))
      result shouldBe List(OneFrameApiResponse(expectedRate.pair.from, expectedRate.pair.to, expectedRate.price, expectedRate.timestamp))
    }
    "return an error an error response from OneFrame" in {
      val client = OneFrameClient.make[IO](mockHttpClient, oneFrameConfig)
      val pair = Rate.Pair(Currency.GBP, Currency.GBP)

      val expectedResponse = OneFrameLookupFailed("Bad Request")
      val result = intercept[OneFrameLookupFailed](client.getRate(pair).unsafeRunSync())

      result.msg shouldBe expectedResponse.msg
    }
  }
}
