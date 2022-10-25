package org.github.ainr.schedule

import org.github.ainr.bot.reaction.BotReactionsInterpreter
import org.github.ainr.infrastructure.context.TrackingIdGen
import org.github.ainr.infrastructure.logger.CustomizedLogger
import org.github.ainr.schedule.tasks.UnsplashUserStatsActivity
import org.github.ainr.subscription.service.SubscriptionService
import org.github.ainr.unsplash.service.UnsplashStatsService

trait ScheduledActivitiesModule {
  def scheduledActivities: ScheduledActivities
}

object ScheduledActivitiesModule {

  def apply(
      logger: CustomizedLogger,
      trackingId: TrackingIdGen,
      botReactions: BotReactionsInterpreter,
      subscriptionService: SubscriptionService,
      unsplashStatsService: UnsplashStatsService
  ): ScheduledActivitiesModule = new ScheduledActivitiesModule {

    val tasks: List[Activity] = List(
      new UnsplashUserStatsActivity(logger, botReactions, subscriptionService, unsplashStatsService)
    )

    override def scheduledActivities: ScheduledActivities =
      ScheduledActivities(tasks)(logger, trackingId)
  }
}
