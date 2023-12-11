package forex.services.rates

import forex.domain.Rate
import forex.services.rates.Errors.Error.OneFrameLookupFailed

trait Algebra[F[_]] {
  def getRatesFromApi: F[OneFrameLookupFailed Either List[Rate]]
}
