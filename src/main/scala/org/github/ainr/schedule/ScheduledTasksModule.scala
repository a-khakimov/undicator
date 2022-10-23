package org.github.ainr.schedule

import org.github.ainr.infrastructure.context.TrackingIdGen
import org.github.ainr.infrastructure.logger.CustomizedLogger
import org.github.ainr.schedule.tasks.TestTask

trait ScheduledTasksModule {
  def scheduledTasks: ScheduledTasks
}

object ScheduledTasksModule {

  def apply(
      logger: CustomizedLogger,
      trackingId: TrackingIdGen
  ): ScheduledTasksModule = new ScheduledTasksModule {

    val tasks: List[Task] = List(
      new TestTask(logger)
    )

    override def scheduledTasks: ScheduledTasks = ScheduledTasks(tasks)(logger, trackingId)
  }
}
