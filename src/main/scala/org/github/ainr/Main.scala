package org.github.ainr

import cats.effect.kernel.{Resource, Sync}
import cats.effect.std.Supervisor
import cats.effect.{ExitCode, IO, IOApp}
import org.github.ainr.bot.BotModule
import org.github.ainr.configurations.Configurations
import org.github.ainr.infrastructure.context.{Context, TrackingIdGen}
import org.github.ainr.infrastructure.logger.CustomizedLogger
import org.github.ainr.schedule.ScheduledTasksModule
import org.http4s
import org.http4s.blaze.client.BlazeClientBuilder
import org.typelevel.log4cats.LoggerName
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp {

  final case class Resources(
      supervisor: Supervisor[IO],
      httpClient: http4s.client.Client[IO]
  )

  def resources: Resource[IO, Resources] = for {
    supervisor <- Supervisor[IO]
    httpClient <- BlazeClientBuilder[IO].resource
  } yield Resources(supervisor, httpClient)

  val app: IO[ExitCode] = for {
    config <- Configurations.load
    context <- Context.make
    trackingIdGen = TrackingIdGen(context)
    logger = CustomizedLogger(
      Slf4jLogger.getLogger[IO](Sync[IO], LoggerName("App")),
      context
    )
    _ <- resources.use { resource =>
      for {
        _ <- logger.info("Resources loaded")
        scheduledTasksModule = ScheduledTasksModule.apply(logger, trackingIdGen)
        _ <- resource.supervisor.supervise(scheduledTasksModule.scheduledTasks.run)
        botModule = BotModule(config.telegram, resource.httpClient)(context, logger, trackingIdGen)
        _ <- logger.info("App started")
        _ <- botModule.longPollBot.start()
      } yield ()
    }
  } yield ExitCode.Success

  override def run(args: List[String]): IO[ExitCode] = app
}
