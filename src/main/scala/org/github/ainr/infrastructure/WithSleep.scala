package org.github.ainr.infrastructure

import cats.Applicative
import cats.effect.Temporal
import cats.syntax.all._

import scala.concurrent.duration.FiniteDuration
import scala.language.implicitConversions

trait WithSleep[T] {
  def withSleep[F[_]: Applicative: Temporal](
      duration: FiniteDuration
  )(
      action: T => F[Unit]
  ): F[Unit]
}

object WithSleep {

  implicit class withSleepForList[T](list: List[T]) extends WithSleep[T] {
    override def withSleep[F[_]: Applicative: Temporal](
        duration: FiniteDuration
    )(
        action: T => F[Unit]
    ): F[Unit] = list.foldLeft[F[Unit]](().pure[F]) {
      (prevG, i) =>
        for {
          _ <- prevG
          _ <- action(i)
          _ <- Temporal[F].sleep(duration)
        } yield ()
    }
  }
}
