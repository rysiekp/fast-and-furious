package com.piktel.fast_and_furious.model.screening

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ScreeningProcessor @Inject()(screeningsTable: ScreeningsTable,
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

  def upsert(newScreening: Screening): Future[Option[Screening]] = {
    val insertion = (table returning table.map(_.id)).insertOrUpdate(newScreening)
    val insertedIdFuture = db.run(insertion)

    val createdCopy: Future[Option[Screening]] = insertedIdFuture.map {
      case Some(resultId) => Some(newScreening.copy(id = Option(resultId)))
      case None => None
    }

    createdCopy
  }

  def removeById(id: Long): Future[Int] = {
    val deletion = (table.filter(_.id === id)).delete
    db.run(deletion)
  }

}
