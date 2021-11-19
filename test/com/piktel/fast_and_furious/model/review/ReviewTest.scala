package com.piktel.fast_and_furious.model.review

import com.piktel.fast_and_furious.model.review.Review.WrongRatingError
import org.scalatest.{FlatSpec, Matchers, TryValues}

import scala.util.{Failure, Success}

class ReviewTest extends FlatSpec with Matchers with TryValues {
  behavior of "Validate rating"

  it should "return Failure for rating not between 1-5" in {
    Review.validateRating(6) shouldBe Failure(WrongRatingError)
  }

  it should "return Success for rating between 1-5" in {
    Review.validateRating(3) shouldBe Success(3)
  }
}
