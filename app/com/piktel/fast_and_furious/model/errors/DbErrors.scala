package com.piktel.fast_and_furious.model.errors

final case class MovieDoesntExistError(private val message: String = "Movie with this id does not exist",
                                       private val cause: Throwable = None.orNull) extends Exception(message, cause)
