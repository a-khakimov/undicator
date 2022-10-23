package org.github.ainr.schedule

import cats.effect.IO
import cats.syntax.all._
import org.github.ainr.infrastructure.Scheduler.schedulerSyntax
import org.github.ainr.infrastructure.context.TrackingIdGen
import org.github.ainr.infrastructure.logger.CustomizedLogger

import java.time.{LocalTime, ZoneId}
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

trait ScheduledTasks {
  def run: IO[Unit]
}

object ScheduledTasks {

  def apply(tasks: List[Task])(
      logger: CustomizedLogger,
      trackingId: TrackingIdGen
  ): ScheduledTasks = {

    new ScheduledTasks {

      override def run: IO[Unit] = {
        tasks
          .traverse {
            case task: OneDayShotTask =>
              trackingId.gen() *> recovered {
                checkTaskTime(task.startTime).flatMap {
                  case moment: Boolean if moment => task.run
                  case _                         => IO.unit
                }
              }
            case task: Task =>
              trackingId.gen() *> recovered {
                task.run
              }
          }
          .every(1 minute)
          .void
      }

      private def recovered(task: IO[Unit]): IO[Unit] =
        task.recover {
          case cause => logger.error(cause)("Recovered from error")
        }

      private def checkTaskTime(time: LocalTime): IO[Boolean] = {
        for {
          now <-
            IO.delay(LocalTime.now(ZoneId.of("Asia/Yekaterinburg"))) // todo: make it configurable
          result = time.isAfter(now) && time.isBefore(now.plusMinutes(1))
        } yield result
      }
    }
  }
}
