package org.github.ainr.unsplash.repository

import cats.effect.IO
import org.github.ainr.infrastructure.logger.CustomizedLogger
import org.github.ainr.unsplash.conf.UnsplashConfig
import org.github.ainr.unsplash.domain.{Photo, Statistics, UserName}
import org.http4s.circe.jsonOf
import org.http4s.client.Client
import org.http4s.headers.{Accept, Authorization}
import org.http4s.{AuthScheme, Credentials, HttpVersion, MediaType, Request, Response, Uri}
import UnsplashRepositoryProtocol._
import org.github.ainr.unsplash.domain.OrderBy.OrderBy

trait UnsplashRepository {

  def getUserStatistics(userName: UserName): IO[Statistics]

  def getUserPhotos(userName: UserName, orderBy: OrderBy, perPage: Long): IO[List[Photo]]
}

object UnsplashRepository {

  def apply(
      conf: UnsplashConfig,
      httpClient: Client[IO],
      logger: CustomizedLogger
  ): UnsplashRepository = new UnsplashRepository {

    val authHeader: Authorization = Authorization(
      Credentials.Token(
        AuthScheme.Bearer,
        conf.token
      )
    )

    val mediaTypeJson: Accept = Accept(MediaType.application.json)
    val request = Request[IO](
      httpVersion = HttpVersion.`HTTP/2`
    ).putHeaders(authHeader, mediaTypeJson)

    private def unsplashErrorHandler(response: Response[IO]): IO[Throwable] = {
      response
        .attemptAs(jsonOf[IO, UnsplashErrorResponse])
        .value
        .map {
          _.map {
            cause =>
              UnsplashException(
                s"Request was failed with status ${response.status.code}, ${cause.errors.mkString(", ")}"
              )
          }.merge
        }
    }

    override def getUserStatistics(userName: UserName): IO[Statistics] = for {
      start <- IO.realTime
      uri <- IO.fromEither(Uri.fromString(s"${conf.url}/users/${userName.toString}/statistics"))
      _ <- logger.info(s"Request to ${uri.toString} started")
      statistics <- httpClient.expectOr(
        request.withUri(
          uri.withQueryParams(Map("resolution" -> "days"))
        )
      )(unsplashErrorHandler)(jsonOf[IO, Statistics])
      end <- IO.realTime
      _ <- logger.info(s"Request to ${uri.toString} completed in ${end - start}")
    } yield statistics

    override def getUserPhotos(
        userName: UserName,
        orderBy: OrderBy,
        perPage: Long
    ): IO[List[Photo]] = for {
      start <- IO.realTime
      uri <- IO.fromEither(Uri.fromString(s"${conf.url}/users/${userName.toString}/photos"))
      _ <- logger.info(s"Request to ${uri.toString} started")
      statistics <- httpClient.expectOr(
        request.withUri(
          uri.withQueryParams(
            Map(
              "resolution" -> "days",
              "order_by" -> orderBy.toString,
              "stats" -> "true",
              "per_page" -> perPage.toString
            )
          )
        )
      )(unsplashErrorHandler)(jsonOf[IO, List[Photo]])
      end <- IO.realTime
      _ <- logger.info(s"Request to ${uri.toString} completed in ${end - start}")
    } yield statistics
  }
}
