package org.github.ainr.schedule.tasks

import cats.effect.IO
import cats.syntax.all._
import org.github.ainr.bot.reaction.{BotReactionsInterpreter, SendPhoto}
import org.github.ainr.infrastructure.Batch
import org.github.ainr.infrastructure.WithSleep.withSleepForList
import org.github.ainr.infrastructure.formatter.Formatter.formatterSyntaxForLong
import org.github.ainr.infrastructure.logger.CustomizedLogger
import org.github.ainr.schedule.OneDayShotActivity
import org.github.ainr.subscription.service.SubscriptionService
import org.github.ainr.unsplash.domain.Statistics
import org.github.ainr.unsplash.service.UnsplashStatsService
import telegramium.bots.{InputPartFile, Markdown2}

import java.time.LocalTime
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

final class UnsplashUserStatsActivity(
    logger: CustomizedLogger,
    botReactions: BotReactionsInterpreter,
    subscriptionService: SubscriptionService,
    unsplashStatsService: UnsplashStatsService
) extends OneDayShotActivity {

  override val name: String = "Send unsplash users stats to subscribers"

  override def startTime: LocalTime = LocalTime.of(20, 0)

  override def run: IO[Unit] = {
    for {
      _ <- logger.info(s"Run UnsplashUserStatsActivity($name)")
      usplashUsers <- subscriptionService.getUnsplashUsers
      usersStats <- Batch.traverse(usplashUsers, 1, 2 seconds)(userName =>
        unsplashStatsService
          .getUserStatistics(userName)
          .map(userName -> _)
      ).map(_.toMap.collect { case (k, Some(v)) => k -> v} )
      _ <- usplashUsers.traverse {
        user =>
          usersStats.get(user).traverse {
            stats =>
              for {
                subscribers <- subscriptionService.getSubscribers(user)
                _ <- logger.info(s"$user subscribers ${subscribers.map(_.id).mkString(",")}")
                _ <- subscribers.withSleep(100 milliseconds) { subscriber =>
                  val reactions = SendPhoto(
                    subscriber,
                    photo = InputPartFile(stats.charts),
                    caption = s"""|```
                              |Stats for @${stats.statistics.username}
                              |Total views \\- ${stats.statistics.views.total.format}
                              |Total downloads \\- ${stats.statistics.downloads.total.format}
                              |Views for last day \\- ${viewsForLastDay(stats.statistics)}
                              |Downloads for last day \\- ${downloadsForLastDay(stats.statistics)}
                              |```""".stripMargin.some,
                    parseMode = Markdown2.some
                  )
                  botReactions.interpret(reactions :: Nil)
                }
              } yield ()
          }
      }
    } yield ()
  }

  private def viewsForLastDay(statistics: Statistics): String =
    statistics.views.historical.values.lastOption
      .map(_.value.format)
      .getOrElse("-")

  private def downloadsForLastDay(statistics: Statistics): String =
    statistics.downloads.historical.values
      .lastOption
      .map(_.value.format)
      .getOrElse("-")
}
