package org.github.ainr.bot

import cats.effect.IO
import cats.syntax.all._
import org.github.ainr.bot.handler.Handler
import org.github.ainr.bot.reaction.{Interpreter, Reaction, SendText, Sleep}
import org.github.ainr.infrastructure.logger.CustomizedLogger
import telegramium.bots.high.implicits.methodOps
import telegramium.bots.high.{Api, LongPollBot, Methods}
import telegramium.bots.{ChatIntId, Message, ParseMode}

object LongPollBot {

  def make(api: Api[IO], handler: Handler)(logger: CustomizedLogger[IO]): LongPollBot[IO] = {

    new LongPollBot[IO](api) with Interpreter {

      override def onMessage(msg: Message): IO[Unit] = {
        for {
          reactions <- handler.handle(msg).recoverWith {
            case cause => logger.error(cause)("Something went wrong").as(Nil)
          }
          _ <- interpret(reactions).recoverWith {
            case cause => logger.error(cause)("Something went wrong")
          }
        } yield ()
      }

      override def interpret(reactions: List[Reaction]): IO[Unit] = {
        reactions.foldLeft(IO.unit) {
          case (prevF, reaction) => prevF.flatMap {
              _ =>
                reaction match {
                  case SendText(chatId, text, parseMode) => sendText(chatId, text, parseMode)
                  case Sleep(delay)                      => IO.sleep(delay)
                }
            }
        }
      }

      private def sendText(
          chatId: ChatIntId,
          text: String,
          parseMode: Option[ParseMode] = None
      ): IO[Unit] =
        Methods
          .sendMessage(chatId, text, parseMode)
          .exec(api)
          .void
    }
  }
}
