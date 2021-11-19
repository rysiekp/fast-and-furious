package com.piktel.fast_and_furious.model.review

import com.piktel.fast_and_furious.model.errors.MovieDoesntExistError
import com.piktel.fast_and_furious.model.movie.{Movie, MovieProcessor, MoviesTable}
import play.api.Configuration
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class ReviewProcessor @Inject()(reviewsTable: ReviewsTable,
                                movieProcessor: MovieProcessor,
                                 protected val dbConfigProvider: DatabaseConfigProvider)
                                (implicit ec: ExecutionContext,
                                 implicit val config: Configuration) extends HasDatabaseConfigProvider[JdbcProfile] {
  import dbConfig.profile.api._

  private val table = TableQuery[reviewsTable.T]

  def list: Future[Seq[Review]] = {
    val reviewsList = table.result
    db.run(reviewsList)
  }

  def create(newReview: Review): Future[Try[Review]] = {
    val movieFuture = movieProcessor.getById(newReview.movieId)

    movieFuture.flatMap(movieOption => movieOption.fold {
      val future: Future[Try[Review]] = Future.successful(Failure(MovieDoesntExistError()))
      future
    } { _ =>
      val future: Future[Try[Review]] = insert(newReview).map(Success(_))
      future
    })
  }

  private def insert(newReview: Review): Future[Review] = {
    val insertion = (table returning table.map(_.id)) += newReview
    val insertedIdFuture = db.run(insertion)

    val createdCopy: Future[Review] = insertedIdFuture.map { resultId =>
      newReview.copy(id = Option(resultId))
    }

    createdCopy
  }
}
