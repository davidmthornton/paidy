package forex.services.rates

import cats.effect.Sync
import forex.domain.{Currency, Price, Rate, Timestamp}
import io.circe.{Decoder, HCursor}
import org.http4s.EntityDecoder
import org.http4s.circe.CirceEntityDecoder

import java.time.{Instant, ZoneOffset}

object Protocol {

  case class OneFrameRate(
                                   from: String,
                                   to: String,
                                   price: BigDecimal,
                                   timestamp: Instant
                                 ) {
    def toDomainRate: Rate =
      Rate(Rate.Pair(Currency.withName(this.from), Currency.withName(this.to)), Price(this.price),
        Timestamp(this.timestamp.atOffset(ZoneOffset.UTC)))
  }

  object OneFrameRate {
    implicit val oneFrameRateDecoder: Decoder[OneFrameRate] = (c: HCursor) => for {
      from <- c.downField("from").as[String]
      to   <- c.downField("to").as[String]
      price   <- c.downField("price").as[BigDecimal]
      timestamp   <- c.downField("time_stamp").as[Instant]

    } yield OneFrameRate(from, to, price, timestamp)

    implicit def ofrListDecoder[F[_] : Sync]: EntityDecoder[F, List[OneFrameRate]] =
      CirceEntityDecoder.circeEntityDecoder
  }



}
