package controllers

import com.piktel.fast_and_furious.model.movie.Movie
import com.piktel.fast_and_furious.model.screening.{Screening, ScreeningProcessor}
import controllers.responses._
import io.swagger.annotations.{Api, ApiImplicitParam, ApiImplicitParams, ApiParam, ApiResponse, ApiResponses}
import play.api.libs.json.Json
import play.api.mvc._

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@Singleton
@Api
class ScreeningsController @Inject()(processor: ScreeningProcessor,
                                     val controllerComponents: ControllerComponents)
                                    (implicit ec: ExecutionContext) extends BaseController {
  @ApiResponse(code = 200, message = "OK", response = classOf[com.piktel.fast_and_furious.model.screening.Screening], responseContainer = "List")
  def list = Action.async { _ =>
    val screeningsFuture: Future[Seq[Screening]] = processor.list
    val response = screeningsFuture.map { screenings =>
      Ok(Json.toJson(SuccessResponse(screenings)))
    }

    response
  }

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "OK", response = classOf[com.piktel.fast_and_furious.model.screening.Screening]),
    new ApiResponse(code = 400, message = "Invalid screening supplied"),
    new ApiResponse(code = 404, message = "Movie not found")
  ))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "id", value = "id of the screening", defaultValue = "null", dataType = "Long", paramType = "body"),
    new ApiImplicitParam(name = "movieId", value = "id of movie being screened", required = true, dataType = "Long", paramType = "body"),
    new ApiImplicitParam(name = "screeningTime", value = "time of the screening", required = true, dataType = "String", paramType = "body"),
    new ApiImplicitParam(name = "price", value = "entry price for the screening", required = true, dataType = "Decimal", paramType = "body")
  ))
  def createOrUpdate = Action.async(parse.json) { request =>
    val incomingBody = request.body.validate[Screening]

    incomingBody.fold(error => {
      val errorMessage = s"Invalid JSON: ${error}"
      val response = BadResponse(BadResponse.INVALID_JSON, errorMessage)
      Future.successful(BadRequest(Json.toJson(response)))
    }, { screening =>
      screening.validate match {
        case Failure(e) => Future.successful(BadRequest(Json.toJson(BadResponse(BAD_REQUEST, e.getMessage))))
        case Success(_) => val createdMovieFuture: Future[Try[Screening]] = processor.upsert(screening)
          createdMovieFuture.map {
            case Success(createdMovie) => Created(Json.toJson(SuccessResponse(createdMovie)))
            case Failure(e) => NotFound(Json.toJson(e.getMessage))
          }
      }
    })
  }

  @ApiResponse(code = 200, message = "OK", response = classOf[com.piktel.fast_and_furious.model.screening.Screening])
  def delete(@ApiParam(value = "id of the screening to delete") id: Long) = Action.async { _ =>
    val deletionFuture: Future[Int] = processor.removeById(id)
    deletionFuture.map { deletion =>
        Ok(Json.toJson(SuccessResponse(deletion)))
    }
  }
}
