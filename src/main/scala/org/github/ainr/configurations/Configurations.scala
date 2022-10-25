package org.github.ainr.configurations

import cats.effect.IO
import cats.syntax.all._
import ciris._
import com.typesafe.config.ConfigFactory
import lt.dvim.ciris.Hocon._
import org.github.ainr.bot.conf.TelegramConfig
import org.github.ainr.infrastructure.db
import org.github.ainr.infrastructure.cache.CacheConfig
import org.github.ainr.unsplash.conf.UnsplashConfig

import scala.concurrent.duration.FiniteDuration

final case class Configurations(
    telegram: TelegramConfig,
    unsplash: UnsplashConfig,
    cacheConfig: CacheConfig,
    postgres: db.postgres.Config
)

object Configurations {

  def load: IO[Configurations] = {

    val config = ConfigFactory.load("reference.conf")

    val telegram = hoconAt(config)("telegram")
    val unsplash = hoconAt(config)("unsplash")
    val postgres = hoconAt(config)("postgres")
    val cache = hoconAt(config)("cache")

    val telegramConfig: ConfigValue[Effect, TelegramConfig] = (
      telegram("url").as[String],
      telegram("token").as[String]
    ).mapN(TelegramConfig.apply)

    val unsplashConfig: ConfigValue[Effect, UnsplashConfig] = (
      unsplash("url").as[String],
      unsplash("token").as[String]
    ).mapN(UnsplashConfig.apply)

    val cacheConfig: ConfigValue[Effect, CacheConfig] = (
      cache("expireAfterWrite").as[FiniteDuration]
    ).map(CacheConfig.apply)

    val postgresConfig: ConfigValue[Effect, db.postgres.Config] = (
      postgres("url").as[String],
      postgres("driver").as[String],
      postgres("user").as[String],
      postgres("password").as[String]
    ).mapN(db.postgres.Config.apply)

    (telegramConfig, unsplashConfig, cacheConfig, postgresConfig)
      .mapN(Configurations.apply)
      .load[IO]
  }
}
