package com.piktel.fast_and_furious.controller

import com.piktel.fast_and_furious.model.review.{Review, ReviewProcessor}
import controllers.ReviewsController
import controllers.responses.{ErrorResponse, SuccessResponse}
import org.mockito.Mockito._
import org.scalatest.TryValues
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test.Helpers.{GET, contentAsJson}
import play.api.test._
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success

class ReviewsControllerTest  extends PlaySpec with TryValues with MockitoSugar {
  implicit private val configuration: Configuration = mock[Configuration]
  private val reviewProcessor: ReviewProcessor = mock[ReviewProcessor]
  private val controller = new ReviewsController(reviewProcessor, Helpers.stubControllerComponents())

  "List" should {
     "return 200 with list of existing reviews" in {
       checkList(Seq(Review(Some(1), 1, 3, None, None)))
     }

    "return 200 with empty list if there are no reviews" in {
      checkList(Seq())
    }

    def checkList(reviews: Seq[Review]): Unit = {
      when(reviewProcessor.list) thenReturn Future.successful(reviews)
      val fakeRequest = FakeRequest(GET, "/reviews/")

      val expectedResponse = Json.toJson(SuccessResponse(reviews))
      val result: Future[Result] = controller.list()(fakeRequest)
      contentAsJson(result) mustBe expectedResponse
    }
  }

  "Create" should {
    val fakeReviewContent = "some_review"
    val fakeAuthor = "Some Author"
    "return 200 for review without an author" in {
      val fakeReview = Review(None, 1, 3, Some(fakeReviewContent), None)
      checkCreate(fakeReview, Json.toJson(SuccessResponse(fakeReview.copy(id = Some(1)))))
    }

    "return 200 for review without a content" in {
      val fakeReview = Review(None, 1, 3, None, Some(fakeAuthor))
      checkCreate(fakeReview, Json.toJson(SuccessResponse(fakeReview.copy(id = Some(1)))))
    }

    "return 200 for a full review" in {
      val fakeReview = Review(None, 1, 3, Some(fakeReviewContent), Some(fakeAuthor))
      checkCreate(fakeReview, Json.toJson(SuccessResponse(fakeReview.copy(id = Some(1)))))
    }

    "return 400 for review with incorrect rating" in {
      val fakeReview = Review(None, 1, 10, None, None)
      checkCreate(fakeReview, Json.toJson(ErrorResponse(BAD_REQUEST, Review.WrongRatingError.getMessage)))
    }

    def checkCreate(review: Review, expectedResponse: JsValue) = {
      val json = Json.toJson(review)
      val fakeRequest = FakeRequest(
        POST,
        "/movies/details/",
        FakeHeaders(Seq(
          ("Accept", "application/json"),
          ("Content-Type", "application/json"))),
        json)
      val newReview = review.copy(id = Some(1))
      when(reviewProcessor.create(review)) thenReturn Future.successful(Success(newReview))
      val result: Future[Result] = controller.create()(fakeRequest)
      contentAsJson(result) mustBe expectedResponse
    }
  }
}
