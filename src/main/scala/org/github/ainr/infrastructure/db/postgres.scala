package org.github.ainr.infrastructure.db

import cats.effect.{Async, Resource, Temporal}
import doobie.hikari.HikariTransactor
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.output.MigrateResult

import scala.concurrent.ExecutionContext

object postgres {

  def transactor[F[_]: Async](
      config: Config,
      ec: ExecutionContext
  ): Resource[F, HikariTransactor[F]] =
    HikariTransactor.newHikariTransactor[F](
      config.driver,
      config.url,
      config.user,
      config.password,
      ec
    )

  def migrate[F[_]: Async: Temporal](config: Config): F[MigrateResult] = {
    Async[F].blocking {
      Flyway
        .configure()
        .dataSource(config.url, config.user, config.password)
        .load()
        .migrate()
    }
  }

  final case class Config(
      url: String,
      driver: String,
      user: String,
      password: String
  ) {
    override def toString: String = {
      s"Postgres configuration: url[$url] driver[$driver] user[$user] password[****]"
    }
  }
}
