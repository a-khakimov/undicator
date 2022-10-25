package org.github.ainr.subscription.service

import cats.effect.IO
import cats.syntax.all._
import org.github.ainr.subscription.repository.SubscriptionRepository
import org.github.ainr.unsplash.domain.UserName
import telegramium.bots.ChatIntId

trait SubscriptionService {

  def addSubscriber(user: UserName, subscriber: ChatIntId): IO[Unit]

  def removeSubscriber(user: UserName, subscriber: ChatIntId): IO[Unit]

  def getSubscribers(user: UserName): IO[List[ChatIntId]]

  def getUnsplashUsers: IO[List[UserName]]

  def getSubscriptions(id: ChatIntId): IO[List[UserName]]
}

object SubscriptionService {

  def make(
      subscriptionRepository: SubscriptionRepository
  ): SubscriptionService = new SubscriptionService {

    override def addSubscriber(user: UserName, subscriber: ChatIntId): IO[Unit] = for {
      subscribers <- subscriptionRepository.getSubscribers(user)
      _ <- subscriptionRepository
        .addSubscriber(user, subscriber)
        .unlessA(subscribers.contains(subscriber))
    } yield ()

    override def removeSubscriber(user: UserName, subscriber: ChatIntId): IO[Unit] =
      subscriptionRepository.removeSubscriber(user, subscriber)

    override def getSubscribers(user: UserName): IO[List[ChatIntId]] =
      subscriptionRepository.getSubscribers(user)

    override def getUnsplashUsers: IO[List[UserName]] =
      subscriptionRepository.getUnsplashUsers

    override def getSubscriptions(id: ChatIntId): IO[List[UserName]] =
      subscriptionRepository.getSubscriptions(id)
  }
}
