package forex.services.rates.client


import cats.effect.BracketThrow
import cats.implicits._
import forex.config.OneFrameConfig
import forex.domain.Rate
import forex.http.rates.Protocol.OneFrameApiResponse
import forex.services.rates.Errors.Error.OneFrameLookupFailed
import org.http4s._
import org.http4s.circe._
import org.http4s.client._
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.headers.Accept
import org.typelevel.ci.CIString

trait OneFrameClient[F[_]] {
  def getRate(pair: Rate.Pair): F[List[OneFrameApiResponse]]
}

object OneFrameClient {

  def make[F[_] : BracketThrow : JsonDecoder](
                                               client: Client[F],
                                               oneFrameConfig: OneFrameConfig,
                                             ): OneFrameClient[F] = new OneFrameClient[F] with Http4sClientDsl[F] {

    val getRateUrl = s"http://${oneFrameConfig.http.host}:${oneFrameConfig.http.port}/rates"

    def getRate(pair: Rate.Pair): F[List[OneFrameApiResponse]] = {

      val currencyPair = Map("pair" -> s"${pair.from}${pair.to}")

      Uri
        .fromString(getRateUrl)
        .liftTo[F]
        .flatMap { uri =>
          val request = Request[F](Method.GET)
            .withUri(uri.withQueryParams(currencyPair))
            .withHeaders(Header.Raw(CIString("token"), oneFrameConfig.token), Accept(MediaType.application.json))

          client.run(request).use { resp =>
            resp.status match {
              case Status.Ok =>
                resp.asJsonDecode[List[OneFrameApiResponse]]
              case status =>
                val msg = Option(status.reason).getOrElse("Unknown error during call to OneFrame service")
                OneFrameLookupFailed(msg).raiseError[F, List[OneFrameApiResponse]]
            }
          }
        }
    }
  }
}
