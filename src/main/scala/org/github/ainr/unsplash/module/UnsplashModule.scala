package org.github.ainr.unsplash.module

import cats.effect.IO
import org.github.ainr.graphs.Graphs
import org.github.ainr.infrastructure.cache.CacheConfig
import org.github.ainr.infrastructure.logger.CustomizedLogger
import org.github.ainr.unsplash.conf.UnsplashConfig
import org.github.ainr.unsplash.repository.{UnsplashRepository, UnsplashRepositoryCached}
import org.github.ainr.unsplash.service.UnsplashStatsService
import org.http4s.client.Client

trait UnsplashModule {
  def unsplashService: UnsplashStatsService
}

object UnsplashModule {

  def apply(
      unsplashConfig: UnsplashConfig,
      cacheConfig: CacheConfig,
      httpClient: Client[IO],
      logger: CustomizedLogger,
      graphs: Graphs
  ): UnsplashModule = new UnsplashModule {

    val unsplashRepository: UnsplashRepository =
      new UnsplashRepositoryCached(
        UnsplashRepository(
          unsplashConfig,
          httpClient,
          logger
        ),
        cacheConfig,
        logger
      )

    override val unsplashService: UnsplashStatsService =
      UnsplashStatsService(unsplashRepository, graphs, logger)
  }
}
