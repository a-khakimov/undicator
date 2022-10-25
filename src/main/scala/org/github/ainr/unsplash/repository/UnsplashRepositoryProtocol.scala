package org.github.ainr.unsplash.repository

import io.circe.{Decoder, HCursor}
import org.github.ainr.unsplash.domain._

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Try

private[repository] object UnsplashRepositoryProtocol {

  private val dateTimeFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd")

  implicit val decodeLocalDate: Decoder[LocalDate] =
    Decoder.decodeString.emapTry {
      str => Try(LocalDate.parse(str, dateTimeFormatter))
    }

  implicit val decodePhotoId: Decoder[PhotoId] =
    Decoder.decodeString.map(PhotoId)

  implicit val decodeDownloadValue: Decoder[Value] = (c: HCursor) =>
    for {
      date <- c.downField("date").as[LocalDate]
      value <- c.downField("value").as[Long]
    } yield Value(date, value)

  implicit val decodeHistorical: Decoder[Historical] =
    (c: HCursor) =>
      for {
        values <- c.downField("values").as[List[Value]]
      } yield Historical(values)

  implicit val decodeDownloads: Decoder[Downloads] = (c: HCursor) =>
    for {
      total <- c.downField("total").as[Long]
      historical <- c.downField("historical").as[Historical]
    } yield Downloads(total, historical)

  implicit val decodeViews: Decoder[Views] = (c: HCursor) =>
    for {
      total <- c.downField("total").as[Long]
      historical <- c.downField("historical").as[Historical]
    } yield Views(total, historical)

  implicit val decodeStatistics: Decoder[Statistics] = (c: HCursor) =>
    for {
      id <- c.downField("id").as[String]
      username <- c.downField("username").as[String]
      downloads <- c.downField("downloads").as[Downloads]
      views <- c.downField("views").as[Views]
    } yield Statistics(id, username, downloads, views)

  implicit val decodePhotoUrls: Decoder[PhotoUrls] = (c: HCursor) =>
    for {
      raw <- c.downField("raw").as[String]
      full <- c.downField("full").as[String]
      regular <- c.downField("regular").as[String]
      small <- c.downField("small").as[String]
      thumb <- c.downField("thumb").as[String]
    } yield PhotoUrls(raw, full, regular, small, thumb)

  implicit val decodePhotoLinks: Decoder[PhotoLinks] = (c: HCursor) =>
    for {
      self <- c.downField("self").as[String]
      html <- c.downField("html").as[String]
      download <- c.downField("download").as[String]
      downloadLocation <- c.downField("download_location").as[String]
    } yield PhotoLinks(self, html, download, downloadLocation)

  implicit val decodePhotoStatistics: Decoder[PhotoStatistics] = (c: HCursor) =>
    for {
      downloads <- c.downField("downloads").as[Downloads]
      views <- c.downField("views").as[Views]
    } yield PhotoStatistics(downloads, views)

  implicit val decodePhoto: Decoder[Photo] = (c: HCursor) =>
    for {
      id <- c.downField("id").as[PhotoId]
      width <- c.downField("width").as[Long]
      height <- c.downField("height").as[Long]
      description <- c.downField("description").as[Option[String]]
      urls <- c.downField("urls").as[PhotoUrls]
      links <- c.downField("links").as[PhotoLinks]
      statistics <- c.downField("statistics").as[PhotoStatistics]
    } yield Photo(id, width, height, description, urls, links, statistics)

  implicit val decodeUnsplashErrorResponse: Decoder[UnsplashErrorResponse] = (c: HCursor) =>
    for {
      errors <- c.downField("errors").as[List[String]]
    } yield UnsplashErrorResponse(errors)
}
