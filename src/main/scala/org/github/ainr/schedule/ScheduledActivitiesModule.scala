package org.github.ainr.schedule

import org.github.ainr.bot.reaction.BotReactionsInterpreter
import org.github.ainr.infrastructure.context.TrackingIdGen
import org.github.ainr.infrastructure.logger.CustomizedLogger
import org.github.ainr.schedule.tasks.TestActivity

trait ScheduledActivitiesModule {
  def scheduledActivities: ScheduledActivities
}

object ScheduledActivitiesModule {

  def apply(
      logger: CustomizedLogger,
      trackingId: TrackingIdGen,
      botReactions: BotReactionsInterpreter
  ): ScheduledActivitiesModule = new ScheduledActivitiesModule {

    val tasks: List[Activity] = List(
      new TestActivity(logger, botReactions)
    )

    override def scheduledActivities: ScheduledActivities =
      ScheduledActivities(tasks)(logger, trackingId)
  }
}
