package com.piktel.fast_and_furious.model.screening

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import javax.inject.Inject

class ScreeningsTable @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {
  import dbConfig.profile.api._
  class T(tag: Tag) extends Table[Screening](tag, "SCREENINGS")  {

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def movieId = column[Long]("MOVIE_ID")
    def screeningTime = column[String]("SCREENING_TIME")
    def price = column[BigDecimal]("PRICE")

    def * = (id.?, movieId, screeningTime, price) <> ((Screening.apply _).tupled, Screening.unapply)
  }
}
