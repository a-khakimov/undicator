package org.github.ainr.infrastructure.logger

import cats.effect.kernel.Sync
import org.typelevel.log4cats.SelfAwareStructuredLogger

trait CustomizedLogger[F[_]] {

  def trace(ctx: Map[String, String])(msg: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): F[Unit]

  def trace(ctx: Map[String, String], t: Throwable)(msg: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): F[Unit]

  def debug(ctx: Map[String, String])(msg: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): F[Unit]

  def debug(ctx: Map[String, String], t: Throwable)(msg: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): F[Unit]

  def info(ctx: Map[String, String])(msg: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): F[Unit]

  def info(ctx: Map[String, String], t: Throwable)(msg: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): F[Unit]

  def warn(ctx: Map[String, String])(msg: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): F[Unit]

  def warn(ctx: Map[String, String], t: Throwable)(msg: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): F[Unit]

  def error(ctx: Map[String, String])(msg: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): F[Unit]

  def error(ctx: Map[String, String], t: Throwable)(msg: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): F[Unit]

  def error(message: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): F[Unit]

  def warn(message: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): F[Unit]

  def info(message: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): F[Unit]

  def debug(message: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): F[Unit]

  def trace(message: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): F[Unit]

  def error(t: Throwable)(message: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): F[Unit]

  def warn(t: Throwable)(message: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): F[Unit]

  def info(t: Throwable)(message: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): F[Unit]

  def debug(t: Throwable)(message: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): F[Unit]

  def trace(t: Throwable)(message: => String)(
      implicit
      name: sourcecode.FullName,
      line: sourcecode.Line
  ): F[Unit]
}

object CustomizedLogger {

  def apply[F[_]: Sync](logger: SelfAwareStructuredLogger[F]): CustomizedLogger[F] = {

    val emptyCtx = Map.empty[String, String]

    implicit class CtxSyntax[T](ctx: Map[String, String]) {
      def withSource(
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): Map[String, String] = {
        ctx.updated(LogKeys.source, s"${name.value} ${line.value}")
      }
    }

    new SelfAwareStructuredLogger[F] with CustomizedLogger[F] {

      override def isTraceEnabled: F[Boolean] = logger.isTraceEnabled

      override def isDebugEnabled: F[Boolean] = logger.isDebugEnabled

      override def isInfoEnabled: F[Boolean] = logger.isInfoEnabled

      override def isWarnEnabled: F[Boolean] = logger.isWarnEnabled

      override def isErrorEnabled: F[Boolean] = logger.isErrorEnabled

      override def trace(ctx: Map[String, String])(msg: => String): F[Unit] = logger.trace(ctx)(msg)

      override def trace(ctx: Map[String, String], t: Throwable)(msg: => String): F[Unit] =
        logger.trace(ctx, t)(msg)

      override def debug(ctx: Map[String, String])(msg: => String): F[Unit] = logger.debug(ctx)(msg)

      override def debug(ctx: Map[String, String], t: Throwable)(msg: => String): F[Unit] =
        logger.debug(ctx, t)(msg)

      override def info(ctx: Map[String, String])(msg: => String): F[Unit] = logger.info(ctx)(msg)

      override def info(ctx: Map[String, String], t: Throwable)(msg: => String): F[Unit] =
        logger.info(ctx, t)(msg)

      override def warn(ctx: Map[String, String])(msg: => String): F[Unit] = logger.warn(ctx)(msg)

      override def warn(ctx: Map[String, String], t: Throwable)(msg: => String): F[Unit] =
        logger.warn(ctx, t)(msg)

      override def error(ctx: Map[String, String])(msg: => String): F[Unit] = logger.error(ctx)(msg)

      override def error(ctx: Map[String, String], t: Throwable)(msg: => String): F[Unit] =
        logger.error(ctx, t)(msg)

      override def error(message: => String): F[Unit] = logger.error(message)

      override def warn(message: => String): F[Unit] = logger.warn(message)

      override def info(message: => String): F[Unit] = logger.info(message)

      override def debug(message: => String): F[Unit] = logger.debug(message)

      override def trace(message: => String): F[Unit] = logger.trace(message)

      override def error(t: Throwable)(message: => String): F[Unit] = logger.error(t)(message)

      override def warn(t: Throwable)(message: => String): F[Unit] = logger.warn(t)(message)

      override def info(t: Throwable)(message: => String): F[Unit] = logger.info(t)(message)

      override def debug(t: Throwable)(message: => String): F[Unit] = logger.debug(t)(message)

      override def trace(t: Throwable)(message: => String): F[Unit] = logger.trace(t)(message)

      def trace(ctx: Map[String, String])(msg: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): F[Unit] = logger.trace(ctx.withSource(name, line))(msg)

      def trace(ctx: Map[String, String], t: Throwable)(msg: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): F[Unit] = logger.trace(ctx.withSource(name, line), t)(msg)

      def debug(ctx: Map[String, String])(msg: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): F[Unit] = logger.debug(ctx.withSource(name, line))(msg)

      def debug(ctx: Map[String, String], t: Throwable)(msg: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): F[Unit] = logger.debug(ctx.withSource(name, line), t)(msg)

      def info(ctx: Map[String, String])(msg: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): F[Unit] = logger.info(ctx.withSource(name, line))(msg)

      def info(ctx: Map[String, String], t: Throwable)(msg: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): F[Unit] = logger.info(ctx.withSource(name, line), t)(msg)

      def warn(ctx: Map[String, String])(msg: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): F[Unit] = logger.warn(ctx.withSource(name, line))(msg)

      def warn(ctx: Map[String, String], t: Throwable)(msg: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): F[Unit] = logger.warn(ctx.withSource(name, line), t)(msg)

      def error(ctx: Map[String, String])(msg: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): F[Unit] = logger.error(ctx.withSource(name, line))(msg)

      def error(ctx: Map[String, String], t: Throwable)(msg: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): F[Unit] = logger.error(ctx.withSource(name, line), t)(msg)

      def error(message: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): F[Unit] = logger.error(emptyCtx.withSource(name, line))(message)

      def warn(message: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): F[Unit] = logger.warn(emptyCtx.withSource(name, line))(message)

      def info(message: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): F[Unit] = logger.info(emptyCtx.withSource(name, line))(message)

      def debug(message: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): F[Unit] = logger.debug(emptyCtx.withSource(name, line))(message)

      def trace(message: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): F[Unit] = logger.trace(emptyCtx.withSource(name, line))(message)

      def error(t: Throwable)(message: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): F[Unit] = logger.error(emptyCtx.withSource(name, line), t)(message)

      def warn(t: Throwable)(message: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): F[Unit] = logger.warn(emptyCtx.withSource(name, line), t)(message)

      def info(t: Throwable)(message: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): F[Unit] = logger.info(emptyCtx.withSource(name, line), t)(message)

      def debug(t: Throwable)(message: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): F[Unit] = logger.debug(emptyCtx.withSource(name, line), t)(message)

      def trace(t: Throwable)(message: => String)(
          implicit
          name: sourcecode.FullName,
          line: sourcecode.Line
      ): F[Unit] = logger.trace(emptyCtx.withSource(name, line), t)(message)
    }
  }
}
