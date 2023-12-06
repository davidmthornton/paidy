package forex.services.rates

import cats.effect.{Async, Resource}
import forex.services.rates.client.OneFrameClient
import forex.services.rates.interpreters._

object Interpreters {

  def live[F[_]: Async](client: => Resource[F, OneFrameClient[F]]): Algebra[F] =
    new LiveOneFrame[F](client)
}
