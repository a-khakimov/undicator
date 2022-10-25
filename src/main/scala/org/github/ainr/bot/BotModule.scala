package org.github.ainr.bot

import cats.effect.IO
import org.github.ainr.bot.conf.TelegramConfig
import org.github.ainr.bot.handler.Handler
import org.github.ainr.bot.handler.handlers.{Start, Subscriptions, UnsplashUserStats, Version}
import org.github.ainr.bot.reaction.BotReactionsInterpreter
import org.github.ainr.infrastructure.context.{Context, TrackingIdGen}
import org.github.ainr.infrastructure.logger.CustomizedLogger
import org.github.ainr.subscription.service.SubscriptionService
import org.github.ainr.unsplash.service.UnsplashStatsService
import org.http4s.client.Client
import telegramium.bots.high.{Api, BotApi, LongPollBot => TgLongPollBot}

trait BotModule {
  def bot: TgLongPollBot[IO] with BotReactionsInterpreter
}

object BotModule {

  def apply(
      config: TelegramConfig,
      httpClient: Client[IO],
      unsplashService: UnsplashStatsService,
      subscriptionService: SubscriptionService
  )(
      context: Context,
      logger: CustomizedLogger,
      trackingIdGen: TrackingIdGen
  ): BotModule = new BotModule {

    private val botApi: Api[IO] = BotApi(
      http = httpClient,
      baseUrl = s"${config.url}/bot${config.token}"
    )

    val unsplashUserStatsHandler = UnsplashUserStats(unsplashService, subscriptionService)(logger)
    val subscriptionsHandler = Subscriptions(subscriptionService)
    val versionHandler = Version()
    val startHandler = Start()

    val handlers: Handler = Handler(
      unsplashUserStatsHandler,
      subscriptionsHandler,
      versionHandler,
      startHandler
    )(logger)

    override val bot: TgLongPollBot[IO] with BotReactionsInterpreter =
      LongPollBot.make(botApi, handlers)(context, logger, trackingIdGen)
  }
}
