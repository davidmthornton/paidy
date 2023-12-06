package forex.http.rates

import forex.domain.Currency
import forex.http.rates.Errors.UnsupportedCurrencyError
import org.http4s.QueryParamDecoder
import org.http4s.dsl.impl.QueryParamDecoderMatcher

import scala.util.Try

object QueryParams {

  private implicit val currencyQueryParam: QueryParamDecoder[Either[UnsupportedCurrencyError, Currency]] =
    QueryParamDecoder[String].map { str =>
      Try(Currency.fromString(str)).toEither.left.map(_ => UnsupportedCurrencyError(s"Invalid currency: $str"))
    }

  object FromQueryParam extends QueryParamDecoderMatcher[Either[UnsupportedCurrencyError, Currency]]("from")
  object ToQueryParam extends QueryParamDecoderMatcher[Either[UnsupportedCurrencyError, Currency]]("to")

}
