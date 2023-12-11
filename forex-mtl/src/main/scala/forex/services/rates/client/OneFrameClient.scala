package forex.services.rates.client

import cats.effect.Sync
import cats.implicits._
import forex.config.OneFrameConfig
import forex.domain.Currency.allPairs
import forex.domain.Rate
import forex.services.rates.Algebra
import forex.services.rates.Errors.Error.OneFrameLookupFailed
import forex.services.rates.Protocol.OneFrameRate
import org.http4s.client._
import org.http4s.headers.Accept
import org.http4s.{Header, MediaType, Method, Request, Uri}
import org.typelevel.ci.CIString

class OneFrameClient[F[_] : Sync](client: Client[F], oneFrameConfig: OneFrameConfig) extends Algebra[F] {

  override def getRatesFromApi: F[OneFrameLookupFailed Either List[Rate]] = {

    val allCurrencyPairsQueryString: String = allPairs.map(pair => "pair=" + pair.from + pair.to).mkString("&")
    val getRateUrl = s"http://${oneFrameConfig.http.host}:${oneFrameConfig.http.port}/rates?$allCurrencyPairsQueryString"

    Uri
      .fromString(getRateUrl)
      .liftTo[F]
      .flatMap { u =>
        val request = Request[F](Method.GET, u)
          .withHeaders(Header.Raw(CIString("token"), oneFrameConfig.token), Accept(MediaType.application.json))

        client.expect[List[OneFrameRate]](request).attempt
          .map {
            case Right(rate) =>
              Right(rate.map(_.toDomainRate))
            case Left(error) =>
              Left(OneFrameLookupFailed(error.getMessage))
          }
      }
  }


}
