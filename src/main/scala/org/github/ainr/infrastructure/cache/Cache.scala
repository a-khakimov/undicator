package org.github.ainr.infrastructure.cache

import cats.effect.IO
import cats.implicits.catsSyntaxApplicativeId
import com.github.blemale.scaffeine
import com.github.blemale.scaffeine.Scaffeine

trait Cache[Key, Value] {

  def cached(key: Key, action: Key => IO[Value]): IO[Value]

  def invalidate(key: Key): IO[Unit]
}

object Cache {

  def apply[Key, Value](config: CacheConfig): Cache[Key, Value] = {

    val cache: scaffeine.Cache[Key, Value] =
      Scaffeine()
        .recordStats()
        .expireAfterWrite(config.expireAfterWrite)
        .build()

    new Cache[Key, Value] {
      override def cached(key: Key, action: Key => IO[Value]): IO[Value] = {
        for {
          fromCache <- IO.delay(cache.getIfPresent(key))
          result <- fromCache.map(_.pure[IO]).getOrElse(action(key))
          _ <- IO.delay(cache.put(key, result))
        } yield result
      }

      override def invalidate(key: Key): IO[Unit] =
        IO.delay(cache.invalidate(key))
    }
  }
}
