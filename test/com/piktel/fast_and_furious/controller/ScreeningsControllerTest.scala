package com.piktel.fast_and_furious.controller

import com.piktel.fast_and_furious.model.review.Review
import controllers.ScreeningsController
import org.scalatest.TryValues
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.Configuration
import play.api.test.Helpers
import com.piktel.fast_and_furious.model.screening.{Screening, ScreeningProcessor}
import controllers.responses.{EndpointResponse, BadResponse, SuccessResponse}
import org.mockito.ArgumentMatchers.any
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

class ScreeningsControllerTest  extends PlaySpec with TryValues with MockitoSugar {
  implicit private val configuration: Configuration = mock[Configuration]
  private val screeningProcessor: ScreeningProcessor = mock[ScreeningProcessor]
  private val controller = new ScreeningsController(screeningProcessor, Helpers.stubControllerComponents())

  "List" should {
    "return 200 with list of existing screenings" in {
      checkList(Seq(Screening(Some(1), 1, "21:37", 21.37)))
    }

    "return 200 with empty list if there are no screenings" in {
      checkList(Seq())
    }

    def checkList(screenings: Seq[Screening]): Unit = {
      when(screeningProcessor.list) thenReturn Future.successful(screenings)
      val fakeRequest = FakeRequest(GET, "/screenings/")

      val expectedResponse = Json.toJson(SuccessResponse(screenings))
      val result: Future[Result] = controller.list()(fakeRequest)
      contentAsJson(result) mustBe expectedResponse
    }
  }

  "CreateOrUpdate" should {
    val fakeScreening = Screening(None, 1, "21:37", 21.37)
    "return 200 for new screening" in {
      checkCreate(fakeScreening, SuccessResponse(fakeScreening.copy(id = Some(1))))
    }

    "return 200 for updated screening" in {
      checkCreate(fakeScreening.copy(id = Some(1)), SuccessResponse(fakeScreening.copy(id = Some(1))))
    }

    "return 400 for screening with incorrect data" in {
      checkCreate(fakeScreening.copy(price = -1), BadResponse(BAD_REQUEST, Screening.NegativePriceError.getMessage))
    }

    def checkCreate(screening: Screening, expectedResponse: EndpointResponse): Unit = {
      val json = Json.toJson(screening)
      val fakeRequest = FakeRequest(
        POST,
        "/screenings/",
        FakeHeaders(Seq(
          ("Accept", "application/json"),
          ("Content-Type", "application/json"))),
        json)
      val newScreening = screening.copy(id = Some(1))
      when(screeningProcessor.upsert(screening)) thenReturn Future.successful(Success(newScreening))
      val result: Future[Result] = controller.createOrUpdate()(fakeRequest)
      contentAsJson(result) mustBe Json.toJson(expectedResponse)
    }
  }

  "Delete" should {
    "return 200 with number of entries deleted" in {
      when(screeningProcessor.removeById(any())).thenReturn(Future.successful(1))
      val fakeRequest = FakeRequest(DELETE, "/screenings/")
      val result: Future[Result] = controller.delete(1)(fakeRequest)
      contentAsJson(result) mustBe Json.toJson(SuccessResponse(1))
    }
  }

}
