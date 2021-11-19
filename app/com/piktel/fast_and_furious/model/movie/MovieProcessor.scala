package com.piktel.fast_and_furious.model.movie

import com.piktel.fast_and_furious.model.omdb.OMDBClient
import play.api.Configuration
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class MovieProcessor @Inject()(moviesTable: MoviesTable,
                                protected val dbConfigProvider: DatabaseConfigProvider)
                              (implicit ec: ExecutionContext,
                               implicit val config: Configuration) extends HasDatabaseConfigProvider[JdbcProfile] {
  import dbConfig.profile.api._

  private val table = TableQuery[moviesTable.T]

  def list: Future[Seq[Movie]] = {
    val moviesList = table.result
    db.run(moviesList)
  }

  def getDetailsById(movieId: Long): Future[Option[OmdbData]] = {
    val movieFuture = getById(movieId)
    getDetails(movieFuture)
  }

  def getDetailsByTitle(title: String): Future[Option[OmdbData]] = {
    val movieFuture = getByTitle(title)
    getDetails(movieFuture)
  }

  private def getDetails(movieFuture: Future[Option[Movie]]): Future[Option[OmdbData]] = {
    movieFuture.map {
      case None => None
      case Some(movie) =>
        val omdbClient = new OMDBClient
        Try { OmdbData(omdbClient.titleById(movie.imdbId, plot = true)) }.toOption
    }
  }

  def create(newMovie: Movie): Future[Movie] = {
    val insertion = (table returning table.map(_.id)) += newMovie
    val insertedIdFuture = db.run(insertion)

    val createdCopy: Future[Movie] = insertedIdFuture.map { resultId =>
      newMovie.copy(id = Option(resultId))
    }

    createdCopy
  }

  def getById(movieId: Long): Future[Option[Movie]] = {
    val movieById = table.filter { f =>
      f.id === movieId
    }.result.headOption
    db.run(movieById)
  }

  def getByTitle(title: String): Future[Option[Movie]] = {
    val movieByTitle = table.filter { f =>
      f.title === title
    }.result.headOption
    db.run(movieByTitle)
  }
}
