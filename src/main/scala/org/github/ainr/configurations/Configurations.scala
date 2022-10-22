package org.github.ainr.conf

import cats.effect.IO
import cats.syntax.all._
import ciris._
import com.typesafe.config.ConfigFactory
import lt.dvim.ciris.Hocon._
import org.github.ainr.bot.conf.TelegramConfig

final case class Config(
    telegram: TelegramConfig
)

object Config {
  def load: IO[Config] = {

    val config = ConfigFactory.load("reference.conf")

    val telegram = hoconAt(config)("telegram")

    val telegramConfig: ConfigValue[Effect, TelegramConfig] = (
      telegram("url").as[String],
      telegram("token").as[String]
    ).mapN(TelegramConfig.apply)

    telegramConfig
      .map(Config.apply)
      .load[IO]
  }
}
