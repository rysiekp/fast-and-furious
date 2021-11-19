package controllers

import com.piktel.fast_and_furious.model.movie.Movie
import com.piktel.fast_and_furious.model.screening.{Screening, ScreeningProcessor}
import controllers.responses._
import play.api.libs.json.Json
import play.api.mvc._

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ScreeningsController @Inject()(processor: ScreeningProcessor,
                                     val controllerComponents: ControllerComponents)
                                    (implicit ec: ExecutionContext) extends BaseController {
  def list = Action.async { _ =>
    val screeningsFuture: Future[Seq[Screening]] = processor.list
    val response = screeningsFuture.map { screenings =>
      Ok(Json.toJson(SuccessResponse(screenings)))
    }

    response
  }

//
//  def getById(movieId: Long) = Action.async { request =>
//    val movieFuture: Future[Option[Movie]] = processor.getById(movieId)
//    movieFuture.map { movie =>
//      movie.fold {
//        NotFound(Json.toJson(ErrorResponse(NOT_FOUND, "No movie found")))
//      } { movie =>
//        Ok(Json.toJson(SuccessResponse(movie)))
//      }
//    }
//  }
//
  def createOrUpdate = Action.async(parse.json) { request =>
    val incomingBody = request.body.validate[Screening]

    incomingBody.fold(error => {
      val errorMessage = s"Invalid JSON: ${error}"
      val response = ErrorResponse(ErrorResponse.INVALID_JSON, errorMessage)
      Future.successful(BadRequest(Json.toJson(response)))
    }, { screening =>
      screening.validate
      val createdMovieFuture: Future[Option[Screening]] = processor.upsert(screening)
      createdMovieFuture.map { createdMovie =>
        Created(Json.toJson(SuccessResponse(createdMovie)))
      }
    })
  }

  def delete(id: Long) = Action.async { _ =>
    val deletionFuture: Future[Int] = processor.removeById(id)
    deletionFuture.map { deletion =>
        Ok(Json.toJson(SuccessResponse(deletion)))
    }
  }
}
