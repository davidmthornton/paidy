package forex.domain

case class Rate(
    pair: Rate.Pair,
    price: Price,
    timestamp: Timestamp
)

object Rate {
  final case class Pair(
      from: Currency,
      to: Currency
  )

  val pairs: Seq[Pair] = Currency.values.flatMap(c => Currency.values.filter(_ != c).map(z => Pair(c, z)))

}
