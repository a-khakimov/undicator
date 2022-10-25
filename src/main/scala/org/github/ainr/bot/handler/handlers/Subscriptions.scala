package org.github.ainr.bot.handler.handlers

import cats.effect.IO
import cats.syntax.all._
import org.github.ainr.bot.reaction.{Reaction, SendText}
import org.github.ainr.subscription.service.SubscriptionService
import telegramium.bots.{ChatIntId, Markdown2, Message}

trait Subscriptions {
  def handle(message: Message): IO[List[Reaction]]
}

object Subscriptions {
  def apply(
      subscriptionService: SubscriptionService
  ): Subscriptions = new Subscriptions {

    override def handle(message: Message): IO[List[Reaction]] = {
      subscriptionService
        .getSubscriptions(ChatIntId(message.chat.id))
        .map(
          _.toNel.map {
            subscriptions =>
              List(
                SendText(
                  chatId = ChatIntId(message.chat.id),
                  parseMode = Markdown2.some,
                  text =
                    s"""```
                     |${subscriptions.map(name => s"@$name").toList.mkString("\n")}
                     |```""".stripMargin
                )
              )
          }.getOrElse {
            List(
              SendText(
                chatId = ChatIntId(message.chat.id),
                text = "You are not subscribed to anyone"
              )
            )
          }
        )
    }
  }
}
