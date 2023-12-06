package forex.domain

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import Currency._

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
  }
}
