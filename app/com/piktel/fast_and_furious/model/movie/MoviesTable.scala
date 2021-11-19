package com.piktel.fast_and_furious.model.movie

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.Inject

class MoviesTable @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {
  import dbConfig.profile.api._
  class T(tag: Tag) extends Table[Movie](tag, "MOVIES")  {

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def imdbId = column[String]("IMDB_ID")
    def title = column[String]("TITLE")

    def * = (id.?, imdbId, title) <> ((Movie.apply _).tupled, Movie.unapply)
  }
}

