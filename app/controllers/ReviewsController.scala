package controllers

import com.piktel.fast_and_furious.model.review.{Review, ReviewProcessor}
import controllers.responses.{ErrorResponse, SuccessResponse}
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc._

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@Singleton
class ReviewsController @Inject()(processor: ReviewProcessor,
                                  val controllerComponents: ControllerComponents)
                                 (implicit ec: ExecutionContext,
                                 implicit val config: Configuration) extends BaseController {
  def list = Action.async { _ =>
    val moviesFuture: Future[Seq[Review]] = processor.list
    val response = moviesFuture.map { movies =>
      Ok(Json.toJson(SuccessResponse(movies)))
    }

    response
  }

  def create = Action.async(parse.json) { request =>
    val incomingBody = request.body.validate[Review]

    incomingBody.fold(error => {
      val errorMessage = s"Invalid JSON: ${error}"
      val response = ErrorResponse(ErrorResponse.INVALID_JSON, errorMessage)
      Future.successful(BadRequest(Json.toJson(response)))
    }, { review =>
      val createdMovieFuture: Future[Try[Review]] = processor.create(review)
      createdMovieFuture.map { createdReview => createdReview match {
        case Failure(e) => BadRequest(Json.toJson(e.getMessage))
        case Success(review) => Created(Json.toJson(SuccessResponse(review)))
      }}
    })
  }
}
