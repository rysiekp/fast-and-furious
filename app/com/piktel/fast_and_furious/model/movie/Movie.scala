package com.piktel.fast_and_furious.model.movie

import play.api.libs.json.{Format, Json}
import xyz.nickr.jomdb.model.TitleResult

case class Movie(id: Option[Long], imdbId: String, title: String)

case class OmdbData(
                   title: String,
                   year: String,
                   rated: String,
                   released: String,
                   runtime: String,
                   genre: String,
                   director: String,
                   writer: String,
                   actors: String,
                   plot: String,
                   language: String,
                   country: String,
                   awards: String,
                   poster: String,
                   metascore: String,
                   imdbRating: String,
                   imdbVotes: String,
                   imdbId: String
                 )

object OmdbData {
  def apply(titleResult: TitleResult): OmdbData = {
    OmdbData(titleResult.getTitle, titleResult.getYear, titleResult.getRated, titleResult.getReleased,
      titleResult.getRuntime, titleResult.getGenre, titleResult.getDirector, titleResult.getWriter,
      titleResult.getActors, titleResult.getPlot, titleResult.getLanguage, titleResult.getCountry, titleResult.getAwards,
      titleResult.getPoster, titleResult.getMetascore, titleResult.getImdbRating, titleResult.getImdbVotes,
      titleResult.getImdbID)
  }

  implicit val format: Format[OmdbData] = Json.format[OmdbData]
}

object Movie {
  implicit val format: Format[Movie] = Json.format[Movie]
}
