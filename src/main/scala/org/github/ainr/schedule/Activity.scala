package org.github.ainr.schedule

import cats.effect.IO

import java.time.LocalTime

trait Activity {

  def run: IO[Unit]

  def name: String
}

trait OneDayShotActivity extends Activity {

  def startTime: LocalTime

  def name: String
}
