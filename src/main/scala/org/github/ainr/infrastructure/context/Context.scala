package org.github.ainr.infrastructure.context

import cats.effect.{IO, IOLocal}

trait Context {

  def set(key: String, value: String): IO[Unit]

  def getAll: IO[Map[String, String]]

  def get(key: String): IO[Unit]
}

object Context {

  def make: IO[Context] = {
    IOLocal(Map.empty[String, String]).map {
      local =>
        new Context {
          override def set(key: String, value: String): IO[Unit] = for {
            current <- local.get.map(context => context.updated(key, value))
            _ <- local.set(current.updated(key, value))
          } yield ()

          override def getAll: IO[Map[String, String]] =
            local.get

          override def get(key: String): IO[Unit] =
            local.get.map(_.get(key))
        }
    }
  }
}
