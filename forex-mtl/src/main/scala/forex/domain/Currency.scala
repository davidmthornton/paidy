package forex.domain

import enumeratum._

sealed trait Currency extends EnumEntry

object Currency extends Enum[Currency] {
  case object AUD extends Currency
  case object CAD extends Currency
  case object CHF extends Currency
  case object EUR extends Currency
  case object GBP extends Currency
  case object NZD extends Currency
  case object JPY extends Currency
  case object SGD extends Currency
  case object USD extends Currency

  override val values: IndexedSeq[Currency] = findValues
}
