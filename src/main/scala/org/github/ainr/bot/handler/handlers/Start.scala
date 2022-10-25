package org.github.ainr.bot.handler.handlers

import cats.effect.IO
import cats.implicits.catsSyntaxApplicativeId
import org.github.ainr.bot.reaction.{Reaction, SendText}
import telegramium.bots.{ChatIntId, Message}

trait Start {

  def handle(message: Message): IO[List[Reaction]]
}

object Start {

  def apply(): Start = new Start {

    override def handle(message: Message): IO[List[Reaction]] = List(
      SendText(
        ChatIntId(message.chat.id),
        "Hello! Send me the unsplash username and I'll show you the statistics of views and downloads of photos of this user"
      )
    ).pure[IO]
  }
}
