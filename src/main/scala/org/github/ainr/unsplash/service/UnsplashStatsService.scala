package org.github.ainr.unsplash.service

import cats.effect.IO
import cats.syntax.all._
import org.github.ainr.graphs.{Graphs, Input}
import org.github.ainr.infrastructure.logger.CustomizedLogger
import org.github.ainr.unsplash.domain.{OrderBy, Photo, Statistics, UserName}
import org.github.ainr.unsplash.repository.UnsplashRepository
import org.nspl

import java.io.File
import java.time.LocalDate

trait UnsplashStatsService {
  def getUserStatistics(userName: UserName): IO[Option[AllUserStatistics]]

  def topUserPhotos(userName: UserName): IO[List[Photo]]
}

object UnsplashStatsService {

  def apply(
      repository: UnsplashRepository,
      graphs: Graphs,
      logger: CustomizedLogger
  ): UnsplashStatsService = new UnsplashStatsService {

    override def getUserStatistics(userName: UserName): IO[Option[AllUserStatistics]] = for {
      statistics <-
        repository
          .getUserStatistics(userName)
          .map(_.some)
          .recoverWith(errorHandler)
      charts <- statistics.traverse(charts)
    } yield (statistics, charts).mapN(AllUserStatistics)

    def topUserPhotos(userName: UserName): IO[List[Photo]] = {
      val maxPhotosNumber = 10 // todo: move to config
      repository.getUserPhotos(userName, OrderBy.popular, maxPhotosNumber)
    }

    private def charts(statistics: Statistics): IO[File] =
      graphs.plot(
        Input(
          List(
            Input.Data(
              statistics.views.historical.values.map(_.value.toDouble),
              s"Views",
              s"Last 30 days (${LocalDate.now().toString})",
              "",
              nspl.Color.green
            ),
            Input.Data(
              statistics.downloads.historical.values.map(_.value.toDouble),
              "Downloads",
              s"unsplash.com/@${statistics.username}",
              "",
              nspl.Color.blue
            )
          )
        )
      )

    def errorHandler: PartialFunction[Throwable, IO[Option[Statistics]]] = {
      case th => logger.error(th)("Something went wrong").as(None)
    }
  }
}
