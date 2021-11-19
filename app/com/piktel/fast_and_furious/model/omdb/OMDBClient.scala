package com.piktel.fast_and_furious.model.omdb

import play.api.Configuration
import xyz.nickr.jomdb.JavaOMDB
import xyz.nickr.jomdb.model.TitleResult

import scala.collection.JavaConverters._

class OMDBClient(implicit val config: Configuration) extends JavaOMDB {
  override def titleById(imdbId: String, plot: Boolean = false): TitleResult = {
    val query = Map(
      "i" -> imdbId,
      "plot" -> (if (plot) "full" else "short"),
      "apikey" -> config.get[String]("omdbApiKey")
    ).asJava

    new TitleResult(this, get(query))
  }
}
