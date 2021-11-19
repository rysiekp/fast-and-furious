package com.piktel.fast_and_furious.model.screening

import com.piktel.fast_and_furious.model.errors.MovieDoesntExistError
import com.piktel.fast_and_furious.model.movie.MovieProcessor
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class ScreeningProcessor @Inject()(screeningsTable: ScreeningsTable,
                                   movieProcessor: MovieProcessor,
                                   protected val dbConfigProvider: DatabaseConfigProvider)
                                  (implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
  import dbConfig.profile.api._

  private val table = TableQuery[screeningsTable.T]

  def list: Future[Seq[Screening]] = db.run(table.result)

  def getById(id: Long): Future[Option[Screening]] = {
    val screeningById = table.filter { f =>
      f.id === id
    }.result.headOption

    db.run(screeningById)
  }

  def upsert(newScreening: Screening): Future[Try[Screening]] = {
    val movieFuture = movieProcessor.getById(newScreening.movieId)

    movieFuture.flatMap { movieOption => movieOption.fold {
      val future: Future[Try[Screening]] = Future.successful(Failure(MovieDoesntExistError()))
      future
    } { _ =>
      val insertion = (table returning table.map(_.id)).insertOrUpdate(newScreening)
      val insertedIdFuture = db.run(insertion)
      insertedIdFuture.map {
        case None => Failure(MovieDoesntExistError())
        case Some(resultId) => Success(newScreening.copy(id = Option(resultId)))
      }
    }}
  }

  def removeById(id: Long): Future[Int] = {
    val deletion = (table.filter(_.id === id)).delete
    db.run(deletion)
  }

}
