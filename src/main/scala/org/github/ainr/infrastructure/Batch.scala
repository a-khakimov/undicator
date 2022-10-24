package org.github.ainr.infrastructure

import cats.effect.IO
import cats.syntax.all._

import scala.concurrent.duration.FiniteDuration

object Batch {
  def traverse[A, B](
      input: List[A],
      batchSize: Int,
      delay: FiniteDuration
  )(f: A => IO[B]): IO[List[B]] = {
    input
      .grouped(batchSize)
      .foldLeft(IO.pure(List[B]())) {
        (accF, batchF) =>
          for {
            acc <- accF
            batch <- batchF.traverse(f)
            _ <- IO.sleep(delay)
          } yield acc ++ batch
      }
  }
}
