package forex.services.rates.client

import cats.effect.{IO, Timer}
import forex.config.{HttpConfig, OneFrameConfig}
import forex.domain.{Currency, Price, Rate, Timestamp}
import forex.services.rates.Errors.Error.OneFrameLookupFailed
import io.circe.Json
import io.circe.parser._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.client.Client
import org.http4s.dsl.Http4sDsl
import org.http4s.{Header, HttpApp, Response, Status}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.typelevel.ci.CIString

import java.time.OffsetDateTime
import scala.concurrent.duration.DurationInt

class RatesClientSpec extends AnyWordSpec with Matchers with Http4sDsl[IO] {
  implicit val timer: Timer[IO] = IO.timer(scala.concurrent.ExecutionContext.Implicits.global)

  object CurrencyQueryString extends QueryParamDecoderMatcher[String]("pair")

  def mockHttpClient(respStatus: Status, respJson: Json): Client[IO] = Client.fromHttpApp(HttpApp[IO] {
    case GET -> _ =>
      IO(Response[IO](respStatus).withEntity(respJson).withHeaders(Header.Raw(CIString("Content-Type"), "application/json")))
  })

  val oneFrameConfig: OneFrameConfig = OneFrameConfig(40.seconds, HttpConfig("http", "localhost", 8080, 40.seconds), "dummy-token")

  "OneFrameClient" should {
    "return a list of rates when the HTTP request to OneFrame is successful" in {

      val respJson = parse(
        """[{"from":"GBP","to":"JPY","bid":0.5493447192669441,"ask":0.3908646611051285,
          |"price":0.4701046901860363,"time_stamp":"2023-12-08T17:41:21.188Z"}]""".stripMargin)
        .getOrElse(Json.fromString(""))

      val client = new OneFrameClient[IO](mockHttpClient(Ok, respJson), oneFrameConfig)

      val result = client.getRatesFromApi.unsafeRunSync()

      val expectedRates = Right(List(Rate(Rate.Pair(Currency.GBP, Currency.JPY), Price(BigDecimal(0.4701046901860363)),
        Timestamp(OffsetDateTime.parse("2023-12-08T17:41:21.188Z")))))

      result shouldBe expectedRates
    }
    "Convert a bad request response to a OneFrameLookupFailed error" in {

      val respJson = parse(
        """{"error":"Invalid Currency Pair"}""".stripMargin)
        .getOrElse(Json.fromString(""))

      val client = new OneFrameClient[IO](mockHttpClient(BadRequest, respJson), oneFrameConfig)

      val result = client.getRatesFromApi.unsafeRunSync()

      result match {
        case Left(OneFrameLookupFailed(msg)) => msg should startWith("unexpected HTTP status: 400 Bad Request")
        case _ => fail("Error didn't match expected format")
      }
    }
  }
}
