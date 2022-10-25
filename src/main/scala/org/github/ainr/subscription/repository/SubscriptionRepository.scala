package org.github.ainr.subscription.repository

import cats.effect.IO
import doobie.Transactor
import doobie.implicits._
import doobie.refined.implicits._
import org.github.ainr.unsplash.domain.UserName
import telegramium.bots.ChatIntId

private[subscription] trait SubscriptionRepository {

  def addSubscriber(user: UserName, subscriber: ChatIntId): IO[Unit]

  def removeSubscriber(user: UserName, subscriber: ChatIntId): IO[Unit]

  def getSubscribers(user: UserName): IO[List[ChatIntId]]

  def getUnsplashUsers: IO[List[UserName]]

  def getSubscriptions(id: ChatIntId): IO[List[UserName]]
}

private[subscription] object SubscriptionRepository {

  def apply(transactor: Transactor[IO]): SubscriptionRepository = new SubscriptionRepository {

    override def addSubscriber(user: UserName, subscriber: ChatIntId): IO[Unit] =
      SQL
        .addSubscriber(user.toString, subscriber.id)
        .run
        .transact(transactor)
        .as(())

    override def removeSubscriber(user: UserName, subscriber: ChatIntId): IO[Unit] =
      SQL
        .removeSubscriber(user.toString, subscriber.id)
        .run
        .transact(transactor)
        .as(())

    override def getSubscribers(user: UserName): IO[List[ChatIntId]] = {
      SQL
        .getSubscribers(user.value)
        .to[List]
        .map(_.map(ChatIntId))
        .transact(transactor)
    }

    override def getUnsplashUsers: IO[List[UserName]] = {
      SQL
        .getUnsplashUsers
        .to[List]
        .transact(transactor)
    }

    override def getSubscriptions(id: ChatIntId): IO[List[UserName]] =
      SQL
        .getSubscriptions(id.id)
        .to[List]
        .transact(transactor)
  }
}

private[repository] object SQL {

  import doobie.implicits.toSqlInterpolator

  def addSubscriber(unsplashUser: String, subscriberTelegramId: Long): doobie.Update0 =
    sql"""
      INSERT INTO subscriptions (
        unsplash_user, subscriber_telegram_id
      ) VALUES ($unsplashUser, $subscriberTelegramId)
    """.update

  def removeSubscriber(unsplashUser: String, subscriberTelegramId: Long): doobie.Update0 =
    sql"""
      DELETE FROM subscriptions
      WHERE unsplash_user = $unsplashUser AND subscriber_telegram_id = $subscriberTelegramId
    """.update

  def getSubscribers(unsplash_user: String): doobie.Query0[Long] =
    sql"SELECT subscriber_telegram_id FROM subscriptions WHERE unsplash_user=$unsplash_user".query

  def getUnsplashUsers: doobie.Query0[UserName] =
    sql"SELECT DISTINCT unsplash_user FROM subscriptions".query

  def getSubscriptions(subscriberTelegramId: Long): doobie.Query0[UserName] =
    sql"""
         SELECT DISTINCT unsplash_user
         FROM subscriptions
         WHERE subscriber_telegram_id=$subscriberTelegramId
         """.query
}
