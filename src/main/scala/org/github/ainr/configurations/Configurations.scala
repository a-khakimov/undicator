package org.github.ainr.configurations

import cats.effect.IO
import cats.syntax.all._
import ciris._
import com.typesafe.config.ConfigFactory
import lt.dvim.ciris.Hocon._
import org.github.ainr.bot.conf.TelegramConfig

final case class Configurations(
    telegram: TelegramConfig
)

object Configurations {

  def load: IO[Configurations] = {

    val config = ConfigFactory.load("reference.conf")

    val telegram = hoconAt(config)("telegram")

    val telegramConfig: ConfigValue[Effect, TelegramConfig] = (
      telegram("url").as[String],
      telegram("token").as[String]
    ).mapN(TelegramConfig.apply)

    telegramConfig
      .map(Configurations.apply)
      .load[IO]
  }
}
