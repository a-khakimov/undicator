package org.github.ainr.schedule.tasks

import cats.effect.IO
import org.github.ainr.bot.reaction.{BotReactionsInterpreter, SendText}
import org.github.ainr.infrastructure.logger.CustomizedLogger
import org.github.ainr.schedule.Activity
import telegramium.bots.ChatIntId

import scala.language.postfixOps

class TestActivity(
    logger: CustomizedLogger,
    botReactions: BotReactionsInterpreter
) extends Activity {

  override val name: String = "Test task"

  override def run: IO[Unit] = {
    for {
      _ <- logger.info("Run test action")
      _ <- botReactions.interpret(
        List(
          SendText(ChatIntId(174861972), "Hello, my little pony!")
        )
      )
    } yield ()
  }
}
