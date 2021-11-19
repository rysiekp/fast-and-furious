package controllers

import com.piktel.fast_and_furious.model.movie.{Movie, MovieProcessor, OmdbData}
import play.api.Configuration

import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import responses._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MoviesController @Inject()(processor: MovieProcessor,
                                  val controllerComponents: ControllerComponents)
                                (implicit ec: ExecutionContext,
                                 implicit val config: Configuration) extends BaseController {
  def list = Action.async { _ =>
    val moviesFuture: Future[Seq[Movie]] = processor.list
    val response = moviesFuture.map { movies =>
      Ok(Json.toJson(SuccessResponse(movies)))
    }

    response
  }

  def getDetailsById(movieId: Long) = Action.async { _ =>
    val movieFuture: Future[Option[OmdbData]] = processor.getDetailsById(movieId)
    movieFuture.map { movie =>
      movie.fold {
        NotFound(Json.toJson(ErrorResponse(NOT_FOUND, "No movie found")))
      } { movie =>
        Ok(Json.toJson(SuccessResponse(movie)))
      }
    }
  }

  def create = Action.async(parse.json) { request =>
    val incomingBody = request.body.validate[Movie]

    incomingBody.fold(error => {
      val errorMessage = s"Invalid JSON: ${error}"
      val response = ErrorResponse(ErrorResponse.INVALID_JSON, errorMessage)
      Future.successful(BadRequest(Json.toJson(response)))
    }, { movie =>
      val createdMovieFuture: Future[Movie] = processor.create(movie)
      createdMovieFuture.map { createdMovie =>
        Created(Json.toJson(SuccessResponse(createdMovie)))
      }
    })


  }
}
