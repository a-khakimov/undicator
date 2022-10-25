package org.github.ainr.unsplash.repository
import cats.effect.IO
import org.github.ainr.infrastructure.cache.{Cache, CacheConfig}
import org.github.ainr.infrastructure.logger.CustomizedLogger
import org.github.ainr.unsplash.domain.OrderBy.OrderBy
import org.github.ainr.unsplash.domain.{Photo, Statistics, UserName}

class UnsplashRepositoryCached(
    repository: UnsplashRepository,
    cacheConfig: CacheConfig,
    logger: CustomizedLogger
) extends UnsplashRepository {

  private val getUserStatisticsCache = Cache[UserName, Statistics](cacheConfig)

  override def getUserStatistics(userName: UserName): IO[Statistics] =
    getUserStatisticsCache.cached(userName, repository.getUserStatistics)

  private val getUserPhotosCache = Cache[(UserName, OrderBy, Long), List[Photo]](cacheConfig)
  override def getUserPhotos(userName: UserName, orderBy: OrderBy, perPage: Long): IO[List[Photo]] =
    getUserPhotosCache.cached(
      (userName, orderBy, perPage),
      {
        case (name, orderBy, perPage) => repository.getUserPhotos(name, orderBy, perPage)
      }
    )

}
