package controllers

import com.piktel.fast_and_furious.model.movie.{Movie, MovieProcessor, OmdbData}
import io.swagger.annotations.{Api, ApiImplicitParam, ApiImplicitParams, ApiParam, ApiResponse, ApiResponses}
import play.api.Configuration

import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import responses.{BadResponse, SuccessResponse}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
@Api
class MoviesController @Inject()(processor: MovieProcessor,
                                  val controllerComponents: ControllerComponents)
                                (implicit ec: ExecutionContext,
                                 implicit val config: Configuration) extends BaseController {
  @ApiResponse(code = 200, message = "OK", response = classOf[com.piktel.fast_and_furious.model.movie.Movie], responseContainer = "List")
  def list = Action.async { _ =>
    val moviesFuture: Future[Seq[Movie]] = processor.list
    moviesFuture.map { movies =>
      Ok(Json.toJson(SuccessResponse(movies)))
    }
  }

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "OK", response = classOf[com.piktel.fast_and_furious.model.movie.OmdbData]),
    new ApiResponse(code = 404, message = "Movie not found")
  ))
  def getDetailsById(@ApiParam(value = "id of the movie to fetch") movieId: Long) = Action.async { _ =>
    val movieFuture: Future[Option[OmdbData]] = processor.getDetailsById(movieId)
    getDetails(movieFuture)
  }

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "OK", response = classOf[com.piktel.fast_and_furious.model.movie.OmdbData]),
    new ApiResponse(code = 404, message = "Movie not found")
  ))
  def getDetailsByTitle(@ApiParam(value = "title of the movie to fetch") title: String) = Action.async { _ =>
    val movieFuture: Future[Option[OmdbData]] = processor.getDetailsByTitle(title)
    getDetails(movieFuture)

  }

  private def getDetails(omdbFuture: Future[Option[OmdbData]]): Future[Result] = {
    omdbFuture.map { movie =>
      movie.fold {
        NotFound(Json.toJson(BadResponse(NOT_FOUND, "No movie found")))
      } { movie =>
        Ok(Json.toJson(SuccessResponse(movie)))
      }
    }
  }

  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "OK", response = classOf[com.piktel.fast_and_furious.model.movie.Movie])
  ))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "id", value = "id of the movie", defaultValue = "null", dataType = "Long", paramType = "body"),
    new ApiImplicitParam(name = "imdbId", value = "OMDB id corresponding to the movie", required = true, dataType = "String", paramType = "body"),
    new ApiImplicitParam(name = "title", value = "title of the movie", required = true, dataType = "String", paramType = "body")
  ))
  def create = Action.async(parse.json) { request =>
    val incomingBody = request.body.validate[Movie]

    incomingBody.fold(error => {
      val errorMessage = s"Invalid JSON: ${error}"
      val response = BadResponse(BadResponse.INVALID_JSON, errorMessage)
      Future.successful(BadRequest(Json.toJson(response)))
    }, { movie =>
      val createdMovieFuture: Future[Movie] = processor.create(movie)
      createdMovieFuture.map { createdMovie =>
        Created(Json.toJson(SuccessResponse(createdMovie)))
      }
    })
  }
}
