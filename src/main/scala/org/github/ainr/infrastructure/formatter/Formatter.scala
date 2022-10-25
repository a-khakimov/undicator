package org.github.ainr.infrastructure.formatter

import java.text.NumberFormat

trait Formatter {

  def format: String

}

object Formatter {

  implicit def formatterSyntaxForLong(long: Long): Formatter = new Formatter {
    override def format: String = formatter.format(long)
  }

  private val formatter: NumberFormat = java.text.NumberFormat.getNumberInstance
}
