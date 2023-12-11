package forex.services.rates

import cats.effect.Concurrent
import forex.config.ApplicationConfig
import forex.services.rates.client.OneFrameClient
import org.http4s.client.Client

object Interpreters {

  def ratesClient[F[_] : Concurrent](oneFrameClient: Client[F], config: ApplicationConfig): Algebra[F] =
    new OneFrameClient[F](oneFrameClient, config.oneFrameConfig)
}
