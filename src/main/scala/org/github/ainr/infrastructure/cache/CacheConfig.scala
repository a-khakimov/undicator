package org.github.ainr.infrastructure.cache

import scala.concurrent.duration.FiniteDuration

final case class CacheConfig(
    expireAfterWrite: FiniteDuration
)
