package controllers

import com.piktel.fast_and_furious.model.review.{Review, ReviewProcessor}
import controllers.responses.{BadResponse, SuccessResponse}
import io.swagger.annotations.{Api, ApiImplicitParam, ApiImplicitParams, ApiResponse, ApiResponses}
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc._

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@Singleton
@Api
class ReviewsController @Inject()(processor: ReviewProcessor,
                                  val controllerComponents: ControllerComponents)
                                 (implicit ec: ExecutionContext,
                                 implicit val config: Configuration) extends BaseController {
  @ApiResponse(code = 200, message = "OK", response = classOf[com.piktel.fast_and_furious.model.review.Review], responseContainer = "List")
  def list = Action.async { _ =>
    val moviesFuture: Future[Seq[Review]] = processor.list
    val response = moviesFuture.map { movies =>
      Ok(Json.toJson(SuccessResponse(movies)))
    }

    response
  }

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "OK", response = classOf[com.piktel.fast_and_furious.model.review.Review]),
    new ApiResponse(code = 400, message = "Invalid review supplied"),
    new ApiResponse(code = 404, message = "Movie not found")
  ))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "id", value = "id of the review", defaultValue = "null", dataType = "Long", paramType = "body"),
    new ApiImplicitParam(name = "movieId", value = "id of movie being reviewed", required = true, dataType = "Long", paramType = "body"),
    new ApiImplicitParam(name = "rating", value = "rating given", required = true, dataType = "Int", paramType = "body"),
    new ApiImplicitParam(name = "review", value = "review text", defaultValue = "null", dataType = "String", paramType = "body"),
    new ApiImplicitParam(name = "author", value = "author of the review", defaultValue = "null", dataType = "String", paramType = "body")
  ))
  def create = Action.async(parse.json) { request =>
    val incomingBody = request.body.validate[Review]

    incomingBody.fold(error => {
      val errorMessage = s"Invalid JSON: ${error}"
      val response = BadResponse(BadResponse.INVALID_JSON, errorMessage)
      Future.successful(BadRequest(Json.toJson(response)))
    }, { review =>
      review.validate match {
        case Failure(e) => Future.successful(BadRequest(Json.toJson(BadResponse(BAD_REQUEST, e.getMessage))))
        case Success(_) => val createdMovieFuture: Future[Try[Review]] = processor.create(review)
          createdMovieFuture.map { createdReview => createdReview match {
            case Failure(e) => NotFound(Json.toJson(e.getMessage))
            case Success(review) => Created(Json.toJson(SuccessResponse(review)))
          }}
      }
    })
  }
}
