package org.github.ainr.bot.handler.handlers

import cats.syntax.all._
import cats.effect.IO
import org.github.ainr.BuildInfo
import org.github.ainr.bot.reaction.{Reaction, SendText}
import telegramium.bots.{ChatIntId, InlineKeyboardButton, InlineKeyboardMarkup, Markdown2, Message}

import java.text.SimpleDateFormat

trait Version {
  def handle(message: Message): IO[List[Reaction]]
}

object Version {
  def apply(): Version = new Version {
    override def handle(message: Message): IO[List[Reaction]] = {
      List(
        SendText(
          chatId = ChatIntId(message.chat.id),
          parseMode = Markdown2.some,
          text =
            s"""```
               |${BuildInfo.name} ${BuildInfo.version}
               |Commit: ${BuildInfo.gitHeadCommit.getOrElse("-")}
               |Build time: ${dateTimeFormat.format(BuildInfo.buildTime)}
               |```""".stripMargin,
          replyMarkup = InlineKeyboardMarkup(
            (InlineKeyboardButton("Github", BuildInfo.github.some) :: Nil) :: Nil
          ).some
        )
      ).pure[IO]
    }

    val dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
  }
}
