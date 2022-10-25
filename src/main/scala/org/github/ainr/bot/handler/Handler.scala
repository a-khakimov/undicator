package org.github.ainr.bot.handler

import cats.effect.IO
import org.github.ainr.bot.handler.handlers.{Start, Subscriptions, UnsplashUserStats, Version}
import org.github.ainr.bot.reaction.Reaction
import org.github.ainr.infrastructure.logger.CustomizedLogger
import telegramium.bots.{CallbackQuery, Message}

trait Handler {
  def onMessage(message: Message): IO[List[Reaction]]

  def onCallbackQuery(query: CallbackQuery): IO[List[Reaction]]
}

object Handler {

  def apply(
      unsplashUserStats: UnsplashUserStats,
      subscriptions: Subscriptions,
      version: Version,
      start: Start
  )(
      logger: CustomizedLogger
  ): Handler = new Handler {

    override def onMessage(message: Message): IO[List[Reaction]] = for {
      _ <- logger.info(s"Message - ${message.text.getOrElse("Empty")}")
      startTime <- IO.realTime
      reactions <- message.text match {
        case Some("/start")         => start.handle(message)
        case Some("/version")       => version.handle(message)
        case Some("/subscriptions") => subscriptions.handle(message)
        case Some(_)                => unsplashUserStats.handleMessage(message)
        case _                      => IO.pure(Nil)
      }
      endTime <- IO.realTime
      _ <- logger.info(s"Request processing time is ${endTime - startTime}")
    } yield reactions

    override def onCallbackQuery(query: CallbackQuery): IO[List[Reaction]] = for {
      _ <- logger.info(s"Message - ${query.message.getOrElse("Empty")}")
      startTime <- IO.realTime
      reactions <- unsplashUserStats.handleQuery(query)
      endTime <- IO.realTime
      _ <- logger.info(s"Request processing time is ${endTime - startTime}")
    } yield reactions
  }
}
