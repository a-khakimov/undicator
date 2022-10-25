package org.github.ainr.unsplash

package object repository {

  final case class UnsplashErrorResponse(
      errors: List[String]
  )

  final case class UnsplashException(
      message: String,
      cause: Option[Throwable] = None
  ) extends Exception(message, cause.orNull)
}
