package org.github.ainr.schedule

import cats.effect.IO
import org.github.ainr.infrastructure.logger.CustomizedLogger
import org.github.ainr.schedule.tasks.TestTask

trait ScheduledTasksModule {
  def scheduledTasks: ScheduledTasks
}

object ScheduledTasksModule {

  def apply(
      implicit
      logger: CustomizedLogger[IO]
  ): ScheduledTasksModule = new ScheduledTasksModule {

    val tasks: List[Task] = List(
      new TestTask
    )

    override def scheduledTasks: ScheduledTasks = ScheduledTasks(tasks)(logger)
  }
}
