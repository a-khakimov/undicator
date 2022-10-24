package org.github.ainr.bot.handler

import cats.effect.IO
import org.github.ainr.bot.reaction.{Reaction, SendText, Sleep}
import org.github.ainr.infrastructure.logger.CustomizedLogger
import telegramium.bots.{ChatIntId, Message}

import scala.concurrent.duration.DurationInt

trait Handler {
  def handle(message: Message): IO[List[Reaction]]
}

object Handler {

  def apply(logger: CustomizedLogger): Handler = new Handler {

    override def handle(message: Message): IO[List[Reaction]] = {
      for {
        _ <- logger.info(s"Message - ${message.text.getOrElse("Empty")}")
        reactions <- IO.pure {
          List(
            SendText(
              ChatIntId(message.chat.id),
              message.text.getOrElse("Empty")
            ),
            Sleep(1 second),
            SendText(
              ChatIntId(message.chat.id),
              message.text.getOrElse("Empty")
            )
          )
        }
        _ <- logger.info(s"Sent reactions")
      } yield reactions
    }
  }
}
