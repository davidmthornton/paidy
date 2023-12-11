package forex.domain

import forex.domain.Currency._
import forex.domain.Rate.Pair
import org.scalacheck.Gen
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

      "contain valid currency pairs" in {
        val currencyPairGen: Gen[Pair] = for {
          curr1 <- Gen.oneOf(Currency.values)
          curr2 <- Gen.oneOf(Currency.values.filterNot(_.entryName == curr1.entryName))
        } yield Pair(curr1, curr2)

        for (_ <- 1 to 1000)
          currencyPairGen.sample map { pair =>
            info(s"Checking pair ${pair.toString}")
            allPairs should contain(pair)
          }
      }
      "not contain any invalid currency pairs" in {
        val invalidPairs: IndexedSeq[Pair] = values map (curr => Pair(curr, curr))

        for (pair <- invalidPairs) {
          info(s"Checking invalid pair ${pair.toString}")
          allPairs shouldNot contain(pair)
        }
      }
    }
  }
}
