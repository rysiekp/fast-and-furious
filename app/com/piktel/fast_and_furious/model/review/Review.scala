package com.piktel.fast_and_furious.model.review

import com.piktel.fast_and_furious.model.review.Review.validateRating
import play.api.libs.json.{Format, Json}

import scala.util.{Failure, Success, Try}

case class Review(id: Option[Long], movieId: Long, rating: Int, review: Option[String], author: Option[String]) {
  def validate: Try[Review] = validateRating(rating).map(_ => this)
}

object Review {
  val WrongRatingError = InvalidReviewException("Rating must be between 1 - 5 starts")

  def validateRating(rating: Int): Try[Int] = {
    rating match {
      case i if i >= 0 && i <= 5 => Success(i)
      case _ => Failure(WrongRatingError)
    }
  }

  implicit val format: Format[Review] = Json.format[Review]

  final case class InvalidReviewException(private val message: String = "Review is invalid",
                                          private val cause: Throwable = None.orNull) extends Exception(message, cause)
}
