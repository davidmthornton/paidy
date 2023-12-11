package forex.services.cache

import forex.domain.Rate
import forex.services.rates.Errors.Error

trait Algebra[F[_]] {
  def initialiseCache(): F[Error Either Unit]
  def getRateFromCache(pair: Rate.Pair): F[Error Either Rate]
}
