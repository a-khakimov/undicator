package org.github.ainr.bot.handler.handlers

import cats.effect.IO
import cats.syntax.all._
import eu.timepit.refined.api.RefType
import eu.timepit.refined.api.RefType.refinedRefType
import org.github.ainr.bot.reaction.{EditMessage, Reaction, SendPhoto, SendText}
import org.github.ainr.infrastructure.formatter.Formatter._
import org.github.ainr.infrastructure.logger.CustomizedLogger
import org.github.ainr.subscription.service.SubscriptionService
import org.github.ainr.unsplash.domain.{Statistics, UserName}
import org.github.ainr.unsplash.service.UnsplashStatsService
import telegramium.bots._

import scala.util.matching.Regex

trait UnsplashUserStats {
  def handleMessage(message: Message): IO[List[Reaction]]

  def handleQuery(query: CallbackQuery): IO[List[Reaction]]
}

object UnsplashUserStats {

  def apply(
      unsplashService: UnsplashStatsService,
      subscriptionService: SubscriptionService
  )(
      logger: CustomizedLogger
  ): UnsplashUserStats = new UnsplashUserStats {

    private def validateUserName(name: String): IO[UserName] = {
      IO.fromEither(RefType.applyRef[UserName](name.toLowerCase)
        .leftMap(UserNameValidationException))
    }

    override def handleMessage(message: Message): IO[List[Reaction]] = {
      for {
        unsplashUserName <- message.text.traverse(validateUserName)
        statsOpt <- unsplashUserName.flatTraverse(unsplashService.getUserStatistics)
        subscribers <- unsplashUserName.traverse(subscriptionService.getSubscribers)
        reactions = statsOpt.map {
          stats =>
            List(
              SendPhoto(
                ChatIntId(message.chat.id),
                photo = InputPartFile(stats.charts)
              ),
              SendText(
                ChatIntId(message.chat.id),
                s"""|```
                   |Stats for @${stats.statistics.username}
                   |Total views \\- ${stats.statistics.views.total.format}
                   |Total downloads \\- ${stats.statistics.downloads.total.format}
                   |Views for last day \\- ${viewsForLastDay(stats.statistics)}
                   |Downloads for last day \\- ${downloadsForLastDay(stats.statistics)}
                   |```""".stripMargin,
                parseMode = Markdown2.some,
                replyMarkup = unsplashUserName.map { name =>
                  InlineKeyboardMarkup(
                    defaultButtons(
                      name, {
                        if (subscribers.toList.flatten.contains(ChatIntId(message.chat.id)))
                          "Unsubscribe"
                        else
                          "Subscribe"
                      }
                    )
                  )
                }
              )
            )
        }.getOrElse(empty(message.chat.id))
        _ <- logger.info(s"Sent reactions")
      } yield reactions
    }.recoverWith {
      error =>
        List(
          SendText(
            ChatIntId(message.chat.id),
            "Send me the unsplash username and I'll show you " +
              "the statistics of views and downloads of photos of this user"
          )
        ).pure[IO] <* logger.error(error)(s"Something went wrong")
    }

    private def defaultButtons(username: UserName, optButtonName: String) = {
      List(
        InlineKeyboardButton(
          text = s"Top photos",
          callbackData = s"Top photos $username".some
        ),
        InlineKeyboardButton(
          text = s"$optButtonName",
          callbackData = s"$optButtonName $username".some
        )
      ) :: Nil
    }

    private def empty(id: Long): List[SendText] = List(
      SendText(
        ChatIntId(id),
        "Stats is empty"
      )
    )

    private def viewsForLastDay(statistics: Statistics): String =
      statistics.views.historical.values.lastOption
        .map(_.value.format)
        .getOrElse("-")

    private def downloadsForLastDay(statistics: Statistics): String =
      statistics.downloads.historical.values
        .lastOption
        .map(_.value.format)
        .getOrElse("-")

    val subscribe: Regex = "(Subscribe) (\\w+)".r
    val unsubscribe: Regex = "(Unsubscribe) (\\w+)".r
    val topPhotos: Regex = "(Top photos) (\\w+)".r

    override def handleQuery(query: CallbackQuery): IO[List[Reaction]] = {
      query.data match {
        case Some(subscribe(_, user))   => subscribeHandle(user, query)
        case Some(unsubscribe(_, user)) => unsubscribeHandle(user, query)
        case Some(topPhotos(_, user))   => topPhotosHandle(user, query)
        case unhandled                  => logger.info(s"Unhandled $unhandled") *> Nil.pure[IO]
      }
    }

    private def subscribeHandle(user: String, query: CallbackQuery): IO[List[Reaction]] =
      for {
        userName <- validateUserName(user)
        _ <- logger.info(s"Subscribe to $userName")
        _ <- logger.info(s"${query.message.map(_.text)}")
        _ <- subscriptionService.addSubscriber(userName, ChatIntId(query.from.id))
      } yield EditMessage(
        chatId = ChatIntId(query.from.id).some,
        messageId = query.message.map(_.messageId),
        parseMode = Markdown2.some,
        replyMarkup = InlineKeyboardMarkup(defaultButtons(userName, "Unsubscribe")).some,
        text = query.message.flatMap(_.text.map(t =>
          s"""```
             |${t.replace("-", "\\-")}
             |```
             |""".stripMargin
        )).getOrElse(s"`@$userName`")
      ) :: Nil

    private def unsubscribeHandle(user: String, query: CallbackQuery): IO[List[Reaction]] =
      for {
        userName <- validateUserName(user)
        _ <- logger.info(s"Unsubscribe from $userName")
        _ <- logger.info(s"${query.message.map(_.text)}")
        _ <- subscriptionService.removeSubscriber(userName, ChatIntId(query.from.id))
      } yield EditMessage(
        chatId = ChatIntId(query.from.id).some,
        messageId = query.message.map(_.messageId),
        parseMode = Markdown2.some,
        replyMarkup = InlineKeyboardMarkup(defaultButtons(userName, "Subscribe")).some,
        text = query.message.flatMap(_.text.map(t =>
          s"""```
             |${t.replace("-", "\\-")}
             |```
             |""".stripMargin
        )).getOrElse(s"`@$userName`")
      ) :: Nil

    private def topPhotosHandle(user: String, query: CallbackQuery): IO[List[Reaction]] =
      for {
        userName <- IO.fromEither(
          RefType.applyRef[UserName](user).leftMap(UserNameValidationException)
        )
        _ <- logger.info(s"Top photos of $userName")
        topPhotos <- unsplashService.topUserPhotos(userName)
        keyboard = topPhotos.map {
          photo =>
            InlineKeyboardButton(
              text =
                s"Views: ${photo.statistics.views.total.format}, " +
                  s"downloads: ${photo.statistics.downloads.total.format}",
              url = photo.links.html.some
            ) :: Nil
        }
      } yield SendText(
        ChatIntId(query.from.id),
        "`Top photos`",
        parseMode = Markdown2.some,
        replyMarkup = InlineKeyboardMarkup(keyboard).some
      ) :: Nil
  }

  final case class UserNameValidationException(
      message: String
  ) extends Exception(message)
}
