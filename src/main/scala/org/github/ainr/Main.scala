package org.github.ainr

import cats.effect.kernel.{Resource, Sync}
import cats.effect.std.Supervisor
import cats.effect.{ExitCode, IO, IOApp}
import org.github.ainr.bot.BotModule
import org.github.ainr.conf.Config
import org.github.ainr.infrastructure.logger.CustomizedLogger
import org.github.ainr.schedule.ScheduledTasksModule
import org.http4s
import org.http4s.blaze.client.BlazeClientBuilder
import org.typelevel.log4cats.LoggerName
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp {

  val logger: CustomizedLogger[IO] = CustomizedLogger(
    Slf4jLogger.getLogger[IO](Sync[IO], LoggerName("App"))
  )

  final case class Resources(
      supervisor: Supervisor[IO],
      httpClient: http4s.client.Client[IO]
  )

  def resources: Resource[IO, Resources] = for {
    supervisor <- Supervisor[IO]
    httpClient <- BlazeClientBuilder[IO].resource
  } yield Resources(supervisor, httpClient)

  val app: IO[ExitCode] = for {
    config <- Config.load
    _ <- resources.use { resource =>
      for {
        _ <- logger.info("Resources loaded")
        scheduledTasksModule = ScheduledTasksModule.apply(logger)
        _ <- resource.supervisor.supervise(scheduledTasksModule.scheduledTasks.run)
        botModule = BotModule(config.telegram, resource.httpClient)(logger)
        _ <- logger.info("App started")
         _ <- botModule.longPollBot.start()
      } yield ()
    }
  } yield ExitCode.Success

  override def run(args: List[String]): IO[ExitCode] = app
}
