package com.piktel.fast_and_furious.controller

import com.piktel.fast_and_furious.model.movie.{Movie, MovieProcessor, OmdbData}
import controllers.MoviesController
import controllers.responses.{BadResponse, SuccessResponse}
import org.scalatest.TryValues
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MoviesControllerTest extends PlaySpec with TryValues with MockitoSugar {
  implicit private val configuration: Configuration = mock[Configuration]
  private val movieProcessor: MovieProcessor = mock[MovieProcessor]
  private val controller = new MoviesController(movieProcessor, Helpers.stubControllerComponents())

  val fakeTitle = "some_title"

  "List" should {
    "return 200 with empty list for empty table" in {
      checkList(Seq())
    }

    "return 200 with nonempty list for nonempty table" in {
      checkList(Seq(Movie(Some(1), "some_imdb_id", "Some title")))
    }

    def checkList(movies: Seq[Movie]): Unit = {
      when(movieProcessor.list) thenReturn Future.successful(movies)
      val fakeRequest = FakeRequest(GET, "/movies/")

      val expectedResponse = Json.toJson(SuccessResponse(movies))
      val result: Future[Result] = controller.list()(fakeRequest)
      contentAsJson(result) mustBe expectedResponse
    }
  }

  "Get details by id" should {
    val fakeMovieId = Some(1L)
    val fakeOmdbDetails = OmdbData(fakeTitle, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")

    "return 200 with movie detail for existing movie" in {
      val mockFuture = Future.successful(Some(fakeOmdbDetails))
      val expectedResponse = Json.toJson(SuccessResponse(fakeOmdbDetails))
      checkGetDetailsById(mockFuture, expectedResponse)
    }

    "return 404 for nonexistent movie" in {
      val mockFuture = Future.successful(None)
      val expectedResponse = Json.toJson(BadResponse(NOT_FOUND, "No movie found"))
      checkGetDetailsById(mockFuture, expectedResponse)
    }

    def checkGetDetailsById(mockFuture: Future[Option[OmdbData]], expectedResponse: JsValue): Unit = {
      val fakeRequest = FakeRequest(GET, "/movies/details/id/")
      when(movieProcessor.getDetailsById(fakeMovieId.get)).thenReturn(mockFuture)
      val result: Future[Result] = controller.getDetailsById(fakeMovieId.get)(fakeRequest)
      contentAsJson(result) mustBe expectedResponse
    }
  }

  "Get details by title" should {
    val fakeOmdbDetails = OmdbData(fakeTitle, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")

    "return 200 with movie detail for existing movie" in {
      val mockFuture = Future.successful(Some(fakeOmdbDetails))
      val expectedResponse = Json.toJson(SuccessResponse(fakeOmdbDetails))
      checkGetDetailsByTitle(mockFuture, expectedResponse)
    }

    "return 404 for nonexistent movie" in {
      val mockFuture = Future.successful(None)
      val expectedResponse = Json.toJson(BadResponse(NOT_FOUND, "No movie found"))
      checkGetDetailsByTitle(mockFuture, expectedResponse)
    }

    def checkGetDetailsByTitle(mockFuture: Future[Option[OmdbData]], expectedResponse: JsValue): Unit = {
      val fakeRequest = FakeRequest(GET, "/movies/details/title/")
      when(movieProcessor.getDetailsByTitle(fakeTitle)).thenReturn(mockFuture)
      val result: Future[Result] = controller.getDetailsByTitle(fakeTitle)(fakeRequest)
      contentAsJson(result) mustBe expectedResponse
    }
  }

  "Create" should {
    val fakeImdbId = "some_imdb_id"
    val fakeMovie = Movie(None, fakeImdbId, fakeTitle)
    "return 200 for valid request" in {
      val json = Json.parse(s"""{"imdbId": "$fakeImdbId", "title": "$fakeTitle"}""")
      val fakeRequest = FakeRequest(
        POST,
        "/movies/details/",
        FakeHeaders(Seq(
          ("Accept", "application/json"),
          ("Content-Type", "application/json"))),
        json)
      when(movieProcessor.create(fakeMovie)) thenReturn Future.successful(fakeMovie.copy(id = Some(1)))
      val results: Future[Result] = controller.create()(fakeRequest)
      val expectedResponse = Json.toJson(SuccessResponse(fakeMovie.copy(id = Some(1))))
      contentAsJson(results) mustBe expectedResponse
    }
  }
}
