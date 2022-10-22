package org.github.ainr.schedule

import cats.effect.IO

import java.time.LocalTime

trait Task {

  def run: IO[Unit]

  def name: String
}

trait OneDayShotTask extends Task {

  def startTime: LocalTime

  def name: String
}
