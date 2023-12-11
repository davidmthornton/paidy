package forex.services.cache.interpreters

import cats.Applicative
import cats.effect.concurrent.Ref
import cats.effect.{Concurrent, Timer}
import cats.implicits._
import forex.config.OneFrameConfig
import forex.domain._
import forex.services.RatesClient
import forex.services.cache.Algebra
import forex.services.rates.Errors.Error
import org.typelevel.log4cats.Logger

import scala.concurrent.duration.FiniteDuration

class RateCache[F[_] : Logger : Timer : Concurrent](oneFrameClient: RatesClient[F], oneFrameConfig: OneFrameConfig) extends Algebra[F] {

  private val cachedRatesMap: Ref[F, Map[Currency, Map[Currency, Rate]]] = Ref.unsafe(Map.empty)

  override def initialiseCache(): F[Either[Error, Unit]] = {
    for {
      _ <- updateCache()
      res <- periodicCacheUpdate(oneFrameConfig.cacheFetchInterval)
    } yield res
  }

  override def getRateFromCache(pair: Rate.Pair): F[Either[Error, Rate]] = {
    Logger[F].info(s"Retrieving ${pair.from}${pair.to} exchange rate from cache") *>
    cachedRatesMap.get.map { cache =>
      cache(pair.from)(pair.to).asRight[Error]
    }
  }

  private def updateCache(): F[Unit] = {
    oneFrameClient.getRatesFromApi.flatMap {
      case Right(rates) if rates.nonEmpty =>
        val newRates = rates.groupBy(_.pair.from).view.mapValues(_.groupBy(_.pair.to).view.mapValues(_.head).toMap).toMap
         cachedRatesMap.set(newRates) *> Logger[F].info(s"Updated exchange rate cache")
      case _ => Applicative[F].unit
    }
  }

  private def periodicCacheUpdate(interval: FiniteDuration): F[Error Either Unit] = {
    val updateAndSchedule = updateCache() >> Timer[F].sleep(interval) >> periodicCacheUpdate(interval)
    Concurrent[F].start(updateAndSchedule).void.map(_.asRight[Error])
  }

}
