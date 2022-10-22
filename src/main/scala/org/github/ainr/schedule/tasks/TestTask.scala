package org.github.ainr.schedule.tasks

import cats.effect.IO
import org.github.ainr.infrastructure.logger.CustomizedLogger
import org.github.ainr.schedule.{OneDayShotTask, Task}

import java.time.LocalTime
import scala.language.postfixOps

class TestTask(
    implicit
    logger: CustomizedLogger[IO]
) extends Task {

  override val name: String = "Test task"

  override def run: IO[Unit] = {
    for {
      _ <- logger.info("Run")
    } yield ()
  }
}
