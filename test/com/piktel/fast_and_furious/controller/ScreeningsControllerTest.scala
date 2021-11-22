package com.piktel.fast_and_furious.controller

import controllers.ScreeningsController
import org.scalatest.TryValues
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.Configuration
import play.api.test.Helpers
import com.piktel.fast_and_furious.model.screening.ScreeningProcessor
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

class ScreeningsControllerTest  extends PlaySpec with TryValues with MockitoSugar {
  implicit private val configuration: Configuration = mock[Configuration]
  private val screeningrocessor: ScreeningProcessor = mock[ScreeningProcessor]
  private val controller = new ScreeningsController(screeningrocessor, Helpers.stubControllerComponents())




}
