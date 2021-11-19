package com.piktel.fast_and_furious.model.screening

import com.piktel.fast_and_furious.model.screening.Screening.{validatePrice, validateScreeningTime}
import org.joda.time.LocalTime
import play.api.libs.json.{Format, Json}

import scala.math.BigDecimal.RoundingMode
import scala.util.{Failure, Success, Try}

case class Screening(id: Option[Long], movieId: Long, screeningTime: String, price: BigDecimal) {
  def validate: Try[Screening] = validateScreeningTime(screeningTime).flatMap(_ => validatePrice(price)).map(_ => this)
}

object Screening {
  val WrongPrecisionError = InvalidPriceException("Price must be set to two decimal points")
  val NegativePriceError = InvalidPriceException("Price cannot be less than 0")

  def validateScreeningTime(screeningTime: String): Try[String] = {
    Try { LocalTime.parse(screeningTime).toString("HH:mm") }
  }

  def validatePrice(price: BigDecimal): Try[BigDecimal] = {
    Try { price.setScale(2, RoundingMode.UNNECESSARY) }
      .recover {
        case _ => throw WrongPrecisionError
      }
      .flatMap { price =>
        if (price < 0) {
          Failure(NegativePriceError)
        } else {
          Success(price)
        }
      }
  }

  implicit val format: Format[Screening] = Json.format[Screening]

  final case class InvalidPriceException(private val message: String = "Price is invalid",
                                         private val cause: Throwable = None.orNull) extends Exception(message, cause)
}
