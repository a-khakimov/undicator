package org.github.ainr.bot

import cats.effect.IO
import org.github.ainr.bot.conf.TelegramConfig
import org.github.ainr.bot.handler.Handler
import org.github.ainr.infrastructure.logger.CustomizedLogger
import org.http4s.client.Client
import telegramium.bots.high.{Api, BotApi, LongPollBot => TgLongPollBot}

trait BotModule {
  def longPollBot: TgLongPollBot[IO]
}

object BotModule {

  def apply(
      config: TelegramConfig,
      httpClient: Client[IO]
  )(
      logger: CustomizedLogger[IO]
  ): BotModule = new BotModule {

    private val botApi: Api[IO] = BotApi(
      http = httpClient,
      baseUrl = s"${config.url}/bot${config.token}"
    )

    val handler: Handler = Handler(logger)

    override def longPollBot: TgLongPollBot[IO] = LongPollBot.make(botApi, handler)(logger)
  }
}
