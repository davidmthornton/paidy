package forex.http.rates.mocks

import cats.effect.IO
import forex.domain.{Price, Rate, Timestamp}
import forex.programs.RatesProgram
import forex.programs.rates.Protocol

import java.time.OffsetDateTime

object Mocks {

  class MockRatesProgram extends RatesProgram[IO] {
    override def get(request: Protocol.GetRatesRequest): IO[Right[Nothing, Rate]] = {
      IO(Right(Rate(Rate.Pair(request.from, request.to), Price(BigDecimal(0.5)), Timestamp(OffsetDateTime.now()))))
    }
  }
}
