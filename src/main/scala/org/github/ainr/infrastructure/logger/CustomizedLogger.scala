package org.github.ainr.infrastructure.logger

import cats.effect.IO
import org.github.ainr.infrastructure.context.Context
import org.typelevel.log4cats.SelfAwareStructuredLogger

trait CustomizedLogger {

  def trace(ctx: Map[String, String])(msg: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): IO[Unit]

  def trace(ctx: Map[String, String], t: Throwable)(msg: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): IO[Unit]

  def debug(ctx: Map[String, String])(msg: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): IO[Unit]

  def debug(ctx: Map[String, String], t: Throwable)(msg: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): IO[Unit]

  def info(ctx: Map[String, String])(msg: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): IO[Unit]

  def info(ctx: Map[String, String], t: Throwable)(msg: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): IO[Unit]

  def warn(ctx: Map[String, String])(msg: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): IO[Unit]

  def warn(ctx: Map[String, String], t: Throwable)(msg: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): IO[Unit]

  def error(ctx: Map[String, String])(msg: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): IO[Unit]

  def error(ctx: Map[String, String], t: Throwable)(msg: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): IO[Unit]

  def error(message: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): IO[Unit]

  def warn(message: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): IO[Unit]

  def info(message: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): IO[Unit]

  def debug(message: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): IO[Unit]

  def trace(message: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): IO[Unit]

  def error(t: Throwable)(message: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): IO[Unit]

  def warn(t: Throwable)(message: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): IO[Unit]

  def info(t: Throwable)(message: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): IO[Unit]

  def debug(t: Throwable)(message: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): IO[Unit]

  def trace(t: Throwable)(message: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): IO[Unit]
}

object CustomizedLogger {

  def apply(
      logger: SelfAwareStructuredLogger[IO],
      context: Context
  ): CustomizedLogger = {

    val emptyLogContext = Map.empty[String, String]

    new SelfAwareStructuredLogger[IO] with CustomizedLogger with LoggerContext {

      override def isTraceEnabled: IO[Boolean] = logger.isTraceEnabled

      override def isDebugEnabled: IO[Boolean] = logger.isDebugEnabled

      override def isInfoEnabled: IO[Boolean] = logger.isInfoEnabled

      override def isWarnEnabled: IO[Boolean] = logger.isWarnEnabled

      override def isErrorEnabled: IO[Boolean] = logger.isErrorEnabled

      override def trace(ctx: Map[String, String])(msg: => String): IO[Unit] =
        logger.trace(ctx)(msg)

      override def trace(ctx: Map[String, String], t: Throwable)(msg: => String): IO[Unit] =
        logger.trace(ctx, t)(msg)

      override def debug(ctx: Map[String, String])(msg: => String): IO[Unit] =
        logger.debug(ctx)(msg)

      override def debug(ctx: Map[String, String], t: Throwable)(msg: => String): IO[Unit] =
        logger.debug(ctx, t)(msg)

      override def info(ctx: Map[String, String])(msg: => String): IO[Unit] =
        logger.info(ctx)(msg)

      override def info(ctx: Map[String, String], t: Throwable)(msg: => String): IO[Unit] =
        logger.info(ctx, t)(msg)

      override def warn(ctx: Map[String, String])(msg: => String): IO[Unit] =
        logger.warn(ctx)(msg)

      override def warn(ctx: Map[String, String], t: Throwable)(msg: => String): IO[Unit] =
        logger.warn(ctx, t)(msg)

      override def error(ctx: Map[String, String])(msg: => String): IO[Unit] =
        logger.error(ctx)(msg)

      override def error(ctx: Map[String, String], t: Throwable)(msg: => String): IO[Unit] =
        logger.error(ctx, t)(msg)

      override def error(message: => String): IO[Unit] =
        logger.error(message)

      override def warn(message: => String): IO[Unit] =
        logger.warn(message)

      override def info(message: => String): IO[Unit] =
        logger.info(message)

      override def debug(message: => String): IO[Unit] =
        logger.debug(message)

      override def trace(message: => String): IO[Unit] =
        logger.trace(message)

      override def error(t: Throwable)(message: => String): IO[Unit] =
        logger.error(t)(message)

      override def warn(t: Throwable)(message: => String): IO[Unit] =
        logger.warn(t)(message)

      override def info(t: Throwable)(message: => String): IO[Unit] =
        logger.info(t)(message)

      override def debug(t: Throwable)(message: => String): IO[Unit] =
        logger.debug(t)(message)

      override def trace(t: Throwable)(message: => String): IO[Unit] =
        logger.trace(t)(message)

      def trace(ctx: Map[String, String])(msg: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): IO[Unit] = for {
        all <- context.getAll
        _ <- logger.trace(
          ctx
            .withSource(name, line)
            .withChatId(all)
            .withTrackingId(all)
        )(msg)
      } yield ()

      def trace(ctx: Map[String, String], t: Throwable)(msg: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): IO[Unit] = for {
        all <- context.getAll
        _ <- logger.trace(
          ctx
            .withSource(name, line)
            .withChatId(all)
            .withTrackingId(all),
          t
        )(msg)
      } yield ()

      def debug(ctx: Map[String, String])(msg: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): IO[Unit] = for {
        all <- context.getAll
        _ <- logger.debug(
          ctx
            .withSource(name, line)
            .withChatId(all)
            .withTrackingId(all)
        )(msg)
      } yield ()

      def debug(ctx: Map[String, String], t: Throwable)(msg: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): IO[Unit] = for {
        all <- context.getAll
        _ <- logger.debug(
          ctx
            .withSource(name, line)
            .withChatId(all)
            .withTrackingId(all),
          t
        )(msg)
      } yield ()

      def info(ctx: Map[String, String])(msg: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): IO[Unit] = for {
        all <- context.getAll
        _ <- logger.info(
          ctx
            .withSource(name, line)
            .withChatId(all)
            .withTrackingId(all)
        )(msg)
      } yield ()

      def info(ctx: Map[String, String], t: Throwable)(msg: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): IO[Unit] = for {
        all <- context.getAll
        _ <- logger.info(
          ctx
            .withSource(name, line)
            .withChatId(all)
            .withTrackingId(all)
        )(msg)
      } yield ()

      def warn(ctx: Map[String, String])(msg: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): IO[Unit] = for {
        all <- context.getAll
        _ <- logger.warn(
          ctx
            .withSource(name, line)
            .withChatId(all)
            .withTrackingId(all)
        )(msg)
      } yield ()

      def warn(ctx: Map[String, String], t: Throwable)(msg: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): IO[Unit] = for {
        all <- context.getAll
        _ <- logger.warn(
          ctx
            .withSource(name, line)
            .withChatId(all)
            .withTrackingId(all),
          t
        )(msg)
      } yield ()

      def error(ctx: Map[String, String])(msg: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): IO[Unit] = for {
        all <- context.getAll
        _ <- logger.error(
          ctx
            .withSource(name, line)
            .withChatId(all)
            .withTrackingId(all)
        )(msg)
      } yield ()

      def error(ctx: Map[String, String], t: Throwable)(msg: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): IO[Unit] = for {
        all <- context.getAll
        _ <- logger.error(
          ctx
            .withSource(name, line)
            .withChatId(all)
            .withTrackingId(all),
          t
        )(msg)
      } yield ()

      def error(message: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): IO[Unit] = for {
        all <- context.getAll
        _ <- logger.error(
          emptyLogContext
            .withSource(name, line)
            .withChatId(all)
            .withTrackingId(all)
        )(message)
      } yield ()

      def warn(message: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): IO[Unit] = for {
        all <- context.getAll
        _ <- logger.warn(
          emptyLogContext
            .withSource(name, line)
            .withChatId(all)
            .withTrackingId(all)
        )(message)
      } yield ()

      def info(message: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): IO[Unit] = for {
        all <- context.getAll
        _ <- logger.info(
          emptyLogContext
            .withSource(name, line)
            .withChatId(all)
            .withTrackingId(all)
        )(message)
      } yield ()

      def debug(message: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): IO[Unit] = for {
        all <- context.getAll
        _ <- logger.debug(
          emptyLogContext
            .withSource(name, line)
            .withChatId(all)
            .withTrackingId(all)
        )(message)
      } yield ()

      def trace(message: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): IO[Unit] = for {
        all <- context.getAll
        _ <- logger.trace(
          emptyLogContext
            .withSource(name, line)
            .withChatId(all)
            .withTrackingId(all)
        )(message)
      } yield ()

      def error(t: Throwable)(message: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): IO[Unit] = for {
        all <- context.getAll
        _ <- logger.error(
          emptyLogContext
            .withSource(name, line)
            .withChatId(all)
            .withTrackingId(all),
          t
        )(message)
      } yield ()

      def warn(t: Throwable)(message: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): IO[Unit] = for {
        all <- context.getAll
        _ <- logger.warn(
          emptyLogContext
            .withSource(name, line)
            .withChatId(all)
            .withTrackingId(all),
          t
        )(message)
      } yield ()

      def info(t: Throwable)(message: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): IO[Unit] = for {
        all <- context.getAll
        _ <- logger.info(
          emptyLogContext
            .withSource(name, line)
            .withChatId(all)
            .withTrackingId(all),
          t
        )(message)
      } yield ()

      def debug(t: Throwable)(message: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): IO[Unit] = for {
        all <- context.getAll
        _ <- logger.debug(
          emptyLogContext
            .withSource(name, line)
            .withChatId(all)
            .withTrackingId(all),
          t
        )(message)
      } yield ()

      def trace(t: Throwable)(message: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): IO[Unit] = for {
        all <- context.getAll
        _ <- logger.trace(
          emptyLogContext
            .withSource(name, line)
            .withChatId(all)
            .withTrackingId(all),
          t
        )(message)
      } yield ()
    }
  }
}
