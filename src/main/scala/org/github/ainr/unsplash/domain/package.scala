package org.github.ainr.unsplash

import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.MatchesRegex

import java.time.LocalDate

package object domain {

  type UserName = String Refined MatchesRegex["""\w+"""]

  final case class Statistics(
      id: String,
      username: String,
      downloads: Downloads,
      views: Views
  )

  final case class Downloads(
      total: Long,
      historical: Historical
  )

  final case class Views(
      total: Long,
      historical: Historical
  )

  final case class Historical(
      values: List[Value]
  )

  final case class Value(
      date: LocalDate,
      value: Long
  )

  final case class PhotoUrls(
      raw: String,
      full: String,
      regular: String,
      small: String,
      thumb: String
  )

  final case class PhotoLinks(
      self: String, // https://api.unsplash.com/photos/yfTZKJ3fnP4
      html: String, // https://unsplash.com/photos/yfTZKJ3fnP4
      download: String, // https://unsplash.com/photos/yfTZKJ3fnP4/download
      downloadLocation: String // https://api.unsplash.com/photos/yfTZKJ3fnP4/download
  )

  final case class PhotoStatistics(
      downloads: Downloads,
      views: Views
  )

  final case class PhotoId(
      value: String
  ) extends AnyVal

  final case class Photo(
      id: PhotoId,
      width: Long,
      height: Long,
      description: Option[String],
      urls: PhotoUrls,
      links: PhotoLinks,
      statistics: PhotoStatistics
  )

  object OrderBy extends Enumeration {
    type OrderBy = Value
    val latest, oldest, popular, views, downloads = Value
  }
}
