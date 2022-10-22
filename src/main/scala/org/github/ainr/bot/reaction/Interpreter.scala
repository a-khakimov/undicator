package org.github.ainr.bot.reaction

import cats.effect.IO

trait Interpreter {

  def interpret(reactions: List[Reaction]): IO[Unit]
}
