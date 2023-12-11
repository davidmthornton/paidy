package forex.domain

import forex.domain.Currency._
import forex.domain.Rate.Pair
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CurrencySpec extends AnyWordSpec with Matchers {

  "Currency" should {
    "return all values" in {
      Currency.values shouldBe Seq(AUD, CAD, CHF, EUR, GBP, NZD, JPY, SGD, USD)
    }
    "Convert correctly to String" in {
      Currency.values.map { c =>
        c.entryName shouldBe c.getClass.getSimpleName.replace("$", "")
      }
    }
    "generate correctly from String" in {
      Currency.values.map { curr =>
        Currency.withName(curr.getClass.getSimpleName.replace("$", "")) shouldBe curr
      }
    }
    "allCurrencyPairs" should {
      "return all valid currency pairs" in {
        allPairs shouldBe Seq(Pair(AUD,CAD), Pair(AUD,CHF), Pair(AUD,EUR), Pair(AUD,GBP), Pair(AUD,NZD), Pair(AUD,JPY), Pair(AUD,SGD), Pair(AUD,USD), Pair(CAD,AUD), Pair(CAD,CHF), Pair(CAD,EUR), Pair(CAD,GBP), Pair(CAD,NZD), Pair(CAD,JPY), Pair(CAD,SGD), Pair(CAD,USD), Pair(CHF,AUD), Pair(CHF,CAD), Pair(CHF,EUR), Pair(CHF,GBP), Pair(CHF,NZD), Pair(CHF,JPY), Pair(CHF,SGD), Pair(CHF,USD), Pair(EUR,AUD), Pair(EUR,CAD), Pair(EUR,CHF), Pair(EUR,GBP), Pair(EUR,NZD), Pair(EUR,JPY), Pair(EUR,SGD), Pair(EUR,USD), Pair(GBP,AUD), Pair(GBP,CAD), Pair(GBP,CHF), Pair(GBP,EUR), Pair(GBP,NZD), Pair(GBP,JPY), Pair(GBP,SGD), Pair(GBP,USD), Pair(NZD,AUD), Pair(NZD,CAD), Pair(NZD,CHF), Pair(NZD,EUR), Pair(NZD,GBP), Pair(NZD,JPY), Pair(NZD,SGD), Pair(NZD,USD), Pair(JPY,AUD), Pair(JPY,CAD), Pair(JPY,CHF), Pair(JPY,EUR), Pair(JPY,GBP), Pair(JPY,NZD), Pair(JPY,SGD), Pair(JPY,USD), Pair(SGD,AUD), Pair(SGD,CAD), Pair(SGD,CHF), Pair(SGD,EUR), Pair(SGD,GBP), Pair(SGD,NZD), Pair(SGD,JPY), Pair(SGD,USD), Pair(USD,AUD), Pair(USD,CAD), Pair(USD,CHF), Pair(USD,EUR), Pair(USD,GBP), Pair(USD,NZD), Pair(USD,JPY), Pair(USD,SGD))
      }
    }
  }
}
