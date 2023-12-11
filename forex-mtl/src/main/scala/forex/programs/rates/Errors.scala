package forex.programs.rates

import forex.programs.rates.Errors.Error.RateLookupFailed
import forex.services.rates.Errors.Error.OneFrameLookupFailed
import forex.services.rates.Errors.{Error => RatesServiceError}

object Errors {

  sealed trait Error extends Exception
  object Error {
    final case class RateLookupFailed(msg: String) extends Error
  }

  def toProgramError(error: RatesServiceError): Error = error match {
    case OneFrameLookupFailed(msg) => RateLookupFailed(msg)
  }
}
