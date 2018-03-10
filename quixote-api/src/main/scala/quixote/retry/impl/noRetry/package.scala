package quixote.retry.impl

import quixote.retry._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/** Very basic implementation: no retry at all ! */
package object noRetry {

  /** Operations on RetryBuilderBase, to get a RetryBuilder */
  implicit class RetryBuilderBaseOps(retryBuilder: RetryBuilderBase) {

    def failable[A](f: => A): FailableRetryBuilder[A] = new FailableRetryBuilder(() => f)

    def tried[A](f: => Try[A]): FailableRetryBuilder[A] = failable(f.get)

    def either[E, S](f: => Either[E, S]): EitherRetryBuilder[E, S] = new EitherRetryBuilder(() => f)

    def future[A](f: => Future[A]): FutureFailableRetryBuilder[A] = new FutureFailableRetryBuilder(() => f)

    def futureEither[E, S](f: => Future[Either[E, S]]): FutureEitherRetryBuilder[E, S] = new FutureEitherRetryBuilder(() => f)
  }

  /** Conversion from a FailableRetryBuilder, to a Future */
  implicit def failableToFuture[A]: FailableToFuture[A] = new FailableToFuture[A]

  /** Conversion from a EitherRetryBuilder, to a Future[Either] */
  implicit def eitherToFutureEither[E, S]: EitherToFuture[E, S] = new EitherToFuture[E, S]

  /** Conversion from a FutureFailableRetryBuilder, to a Future */
  implicit def futureFailableToFuture[A]: FutureFailableToFuture[A] = new FutureFailableToFuture[A]

  /** Conversion from a FutureEitherRetryBuilder, to a Future[Either] */
  implicit def futureEitherToFutureEither[E, S]: FutureEitherToFuture[E, S] = new FutureEitherToFuture[E, S]

  /** Operations on RetryBuilder */
  implicit class RetryBuilderOps[R <: RetryBuilder[_, _]](retryBuilder: R) {
    def toFuture[A](implicit typeclass: R >>> Future[A]): Future[A] = typeclass.convert(retryBuilder)
  }

}
