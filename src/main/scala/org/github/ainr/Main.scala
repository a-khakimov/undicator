package org.github.ainr

import cats.effect.kernel.{Resource, Sync}
import cats.effect.std.Supervisor
import cats.effect.{ExitCode, IO, IOApp}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import org.github.ainr.bot.BotModule
import org.github.ainr.configurations.Configurations
import org.github.ainr.graphs.Graphs
import org.github.ainr.infrastructure.context.{Context, TrackingIdGen}
import org.github.ainr.infrastructure.db
import org.github.ainr.infrastructure.logger.CustomizedLogger
import org.github.ainr.schedule.ScheduledActivitiesModule
import org.github.ainr.subscription.module.SubscriptionModule
import org.github.ainr.unsplash.module.UnsplashModule
import org.http4s
import org.http4s.blaze.client.BlazeClientBuilder
import org.typelevel.log4cats.LoggerName
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp {

  final case class Resources(
      supervisor: Supervisor[IO],
      httpClient: http4s.client.Client[IO],
      transactor: HikariTransactor[IO]
  )

  def resources(configurations: Configurations): Resource[IO, Resources] = for {
    supervisor <- Supervisor[IO]
    httpClient <- BlazeClientBuilder[IO].resource
    transactorEc <- ExecutionContexts.fixedThreadPool[IO](5) // todo: move to config
    transactor <- db.postgres.transactor[IO](configurations.postgres, transactorEc)
  } yield Resources(supervisor, httpClient, transactor)

  val app: IO[ExitCode] = for {
    context <- Context.make
    trackingIdGen = TrackingIdGen(context)
    logger = CustomizedLogger(
      Slf4jLogger.getLogger[IO](Sync[IO], LoggerName("App")),
      context
    )
    config <- Configurations.load.onError(
      logger.error(_)("Configuration loading failed")
    )
    _ <- db.postgres.migrate[IO](config.postgres)
    _ <- logger.info("Postgres migration ok")
    _ <- resources(config).use { resource =>
      for {
        _ <- logger.info("Resources loaded")
        unsplashModule = UnsplashModule(
          config.unsplash,
          config.cacheConfig,
          resource.httpClient,
          logger,
          Graphs()
        )
        _ <- logger.info("UnsplashModule ok")
        subscriptionModule = SubscriptionModule(
          resource.transactor
        )
        _ <- logger.info("SubscriptionModule ok")
        botModule = BotModule(
          config.telegram,
          resource.httpClient,
          unsplashModule.unsplashService,
          subscriptionModule.subscriptionService
        )(
          context,
          logger,
          trackingIdGen
        )
        _ <- logger.info("SubscriptionModule ok")
        scheduledTasksModule = ScheduledActivitiesModule(
          logger,
          trackingIdGen,
          botModule.bot,
          subscriptionModule.subscriptionService,
          unsplashModule.unsplashService
        )
        _ <- logger.info("ScheduledActivitiesModule ok")
        _ <- resource.supervisor.supervise(scheduledTasksModule.scheduledActivities.run)
        _ <- botModule.bot.start()
      } yield ()
    }
  } yield ExitCode.Success

  override def run(args: List[String]): IO[ExitCode] = app.handleErrorWith {
    th =>
      cats.effect.std.Console[IO]
        .error(s"App failed with: ${th.getMessage} ${th.printStackTrace()}")
        .as(ExitCode.Error)
  }
}
