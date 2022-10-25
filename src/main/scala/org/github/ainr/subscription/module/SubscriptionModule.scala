package org.github.ainr.subscription.module

import cats.effect.IO
import doobie.Transactor
import org.github.ainr.subscription.repository.SubscriptionRepository
import org.github.ainr.subscription.service.SubscriptionService

trait SubscriptionModule {
  def subscriptionService: SubscriptionService
}

object SubscriptionModule {

  def apply(transactor: Transactor[IO]): SubscriptionModule = new SubscriptionModule {

    val subscriptionRepository: SubscriptionRepository =
      SubscriptionRepository(transactor)

    override val subscriptionService: SubscriptionService =
      SubscriptionService.make(subscriptionRepository)
  }
}
