package com.piktel.fast_and_furious.model.review

import com.piktel.fast_and_furious.model.screening.Screening
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.Inject

class ReviewsTable @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {
  import dbConfig.profile.api._
  class T(tag: Tag) extends Table[Review](tag, "REVIEWS")  {

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def movieId = column[Long]("MOVIE_ID")
    def rating = column[Int]("RATING")
    def review = column[String]("REVIEW")
    def author = column[String]("AUTHOR")

    def * = (id.?, movieId, rating, review.?, author.?) <> ((Review.apply _).tupled, Review.unapply)
  }
}
