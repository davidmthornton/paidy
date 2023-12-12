package forex.http
package rates

import cats.effect.Sync
import cats.implicits._
import forex.domain.{Currency, Rate}
import forex.http.rates.Errors._
import forex.programs.RatesProgram
import forex.programs.rates.Errors.{Error => ProgramError}
import forex.programs.rates.{Protocol => RatesProgramProtocol}
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.{HttpRoutes, Response}

class RatesHttpRoutes[F[_]: Sync](rates: RatesProgram[F]) extends Http4sDsl[F] {

  import Converters._
  import QueryParams._

  private[http] val prefixPath = "/rates"

  val F: Sync[F] = Sync[F]

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root :? FromQueryParam(maybeFromCurrency) +& ToQueryParam(maybeToCurrency) =>
      (for {
        from <- F.fromEither(maybeFromCurrency)
        to <- F.fromEither(maybeToCurrency)
        res <- handleSameCurrency(from, to) *> rates.get(RatesProgramProtocol.GetRatesRequest(from, to))
        result <- handleRatesGetResponse(res)
      } yield result)
        .recoverWith {
          case invalidCurr@InvalidSuppliedCurrencyError(_) => BadRequest(invalidCurr)
          case unsuppCurr@UnsupportedCurrencyError(_) => BadRequest(unsuppCurr)
        }
  }

  private def handleRatesGetResponse(resp: ProgramError Either Rate): F[Response[F]] = {
    resp match {
      case Right(rate) => Ok(rate.asGetApiResponse)
      case Left(error) => BadRequest(UnsupportedCurrencyError(error.getMessage))
    }
  }

  private def handleSameCurrency(to: Currency, from: Currency): F[Unit] = {
    if (from == to)
      F.raiseError[Unit](InvalidSuppliedCurrencyError(
        s"Cannot convert to and from the same currency. Invalid conversion requested: $from to $to")
      )
    else
      F.unit
  }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )

}
