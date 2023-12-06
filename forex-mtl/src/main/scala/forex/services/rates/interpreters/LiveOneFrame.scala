package forex.services.rates.interpreters

import cats.effect.{BracketThrow, Resource}
import cats.implicits.toFunctorOps
import cats.syntax.either._
import forex.domain.Rate
import forex.services.rates.Algebra
import forex.services.rates.Errors.Error.OneFrameLookupFailed
import forex.services.rates.Errors._
import forex.services.rates.client.OneFrameClient

class LiveOneFrame[F[_]: BracketThrow](client: Resource[F, OneFrameClient[F]]) extends Algebra[F] {

  override def get(pair: Rate.Pair): F[Error Either Rate] =
    client.use { client =>
      client.getRate(pair)
    }.map (rates => rates.find(_.to == pair.to) match {
      case Some(rate) => Rate(pair, rate.price, rate.timeStamp).asRight
      case None => OneFrameLookupFailed(s"No exchange rate found for pair $pair").asLeft
  })
}
