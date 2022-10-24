package org.github.ainr.infrastructure

import cats.Monad
import cats.effect.Temporal
import cats.syntax.all._

import scala.concurrent.duration.FiniteDuration
import scala.language.implicitConversions

final class Scheduler[F[_]: Temporal: Monad, T](action: F[T]) {
  def every(duration: FiniteDuration): F[T] = for {
    _ <- action
    _ <- Temporal[F].sleep(duration)
    result <- every(duration)
  } yield result
}

object Scheduler {

  implicit def schedulerSyntax[F[_]: Temporal: Monad, T](action: F[T]): Scheduler[F, T] =
    new Scheduler[F, T](action)
}
