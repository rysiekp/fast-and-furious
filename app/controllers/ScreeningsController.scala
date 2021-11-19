package controllers

import com.piktel.fast_and_furious.model.movie.Movie
import com.piktel.fast_and_furious.model.screening.{Screening, ScreeningProcessor}
import controllers.responses._
import play.api.libs.json.Json
import play.api.mvc._

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

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

  def createOrUpdate = Action.async(parse.json) { request =>
    val incomingBody = request.body.validate[Screening]

    incomingBody.fold(error => {
      val errorMessage = s"Invalid JSON: ${error}"
      val response = ErrorResponse(ErrorResponse.INVALID_JSON, errorMessage)
      Future.successful(BadRequest(Json.toJson(response)))
    }, { screening =>
      screening.validate match {
        case Failure(e) => Future.successful(BadRequest(Json.toJson(ErrorResponse(BAD_REQUEST, e.getMessage))))
        case Success(_) => val createdMovieFuture: Future[Try[Screening]] = processor.upsert(screening)
          createdMovieFuture.map {
            case Success(createdMovie) => Created(Json.toJson(SuccessResponse(createdMovie)))
            case Failure(e) => BadRequest(Json.toJson(e.getMessage))
          }
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
