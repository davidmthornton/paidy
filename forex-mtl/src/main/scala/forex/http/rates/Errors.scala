package forex.http.rates

import io.circe._
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredEncoder

object Errors {

  implicit val configuration: Configuration = Configuration.default.withSnakeCaseMemberNames

  implicit val unsupportedCurrencyErrorEncoder: Encoder[UnsupportedCurrencyError] =
    deriveConfiguredEncoder[UnsupportedCurrencyError]

  implicit val invalidSuppliedCurrencyErrorEncoder: Encoder[InvalidSuppliedCurrencyError] =
    deriveConfiguredEncoder[InvalidSuppliedCurrencyError]

  trait Error {
    val error: String
  }
  final case class UnsupportedCurrencyError(error: String) extends Throwable with Error
  final case class InvalidSuppliedCurrencyError(error: String) extends Throwable with Error
}
