package org.github.ainr.bot.reaction

import cats.effect.IO

trait BotReactionsInterpreter {

  def interpret(reactions: List[Reaction]): IO[Unit]
}
