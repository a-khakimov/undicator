package org.github.ainr.infrastructure.context

import cats.effect.IO
import cats.effect.std.UUIDGen
import org.github.ainr.infrastructure.logger.LogKeys

trait TrackingIdGen {

  def gen(): IO[Unit]
}

object TrackingIdGen {

  def apply(context: Context): TrackingIdGen = new TrackingIdGen {

    override def gen(): IO[Unit] = for {
      trackingID <- UUIDGen.randomString[IO]
      _ <- context.set(LogKeys.trackingID, trackingID.take(8).toUpperCase)
    } yield ()
  }
}
