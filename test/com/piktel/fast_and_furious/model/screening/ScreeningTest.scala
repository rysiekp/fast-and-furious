package com.piktel.fast_and_furious.model.screening

import com.piktel.fast_and_furious.model.screening.Screening.{InvalidPriceException, NegativePriceError, WrongPrecisionError}
import org.joda.time.IllegalFieldValueException
import org.scalatest.{FlatSpec, Matchers, TryValues}

import scala.util.{Failure, Success}

class ScreeningTest extends FlatSpec with Matchers with TryValues {
  behavior of "Validate price"

  it should "return Failure for negative price" in {
    Screening.validatePrice(-10) shouldBe Failure(NegativePriceError)
  }

  it should "return Failure for wrong precision price" in {
    Screening.validatePrice(21.037) shouldBe Failure(WrongPrecisionError)
  }

  it should "return Success for correct price" in {
    Screening.validatePrice(21.37) shouldBe Success(21.37)
  }

  behavior of "Validate screening time"

  it should "return Success for correct screening time" in {
    Screening.validateScreeningTime("14:00") shouldBe Success("14:00")
  }

  it should "return Failure for incorrect screening time" in {
    Screening.validateScreeningTime("37:21").failure.exception shouldBe an [IllegalFieldValueException]
  }
}
