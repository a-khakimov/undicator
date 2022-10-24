package org.github.ainr.bot.reaction

import telegramium.bots.{ChatIntId, ParseMode}

import scala.concurrent.duration.FiniteDuration

trait Reaction

final case class Sleep(
    delay: FiniteDuration
) extends Reaction

final case class SendText(
    chatId: ChatIntId,
    text: String,
    parseMode: Option[ParseMode] = None
) extends Reaction

final case class SendDocument() extends Reaction

final case class SendPhoto() extends Reaction
