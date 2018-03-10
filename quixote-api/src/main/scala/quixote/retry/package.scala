package quixote

import quixote.configuration.Configuration

import scala.concurrent.Future
import scala.util.Try

package object retry {

  type >>>[R <: RetryBuilder[_, _], T] = ConvertsTo[R, T]

  def retry: RetryBuilderBase = RetryBuilderBase.default

  def retry(name: String): RetryBuilderBase = RetryBuilderBase.named(name)

  def retry(config: Configuration): RetryBuilderBase = RetryBuilderBase.custom(config)

  /** Operations on RetryBuilderBase, to get a RetryBuilder */
  implicit class RetryBuilderBaseOps(rbb: RetryBuilderBase) {

    def failable[A](f: => A): FailableRetryBuilder[A] = new FailableRetryBuilder(rbb.conf, () => f)

    def tried[A](f: => Try[A]): FailableRetryBuilder[A] = failable(f.get)

    def either[E, S](f: => Either[E, S]): EitherRetryBuilder[E, S] = new EitherRetryBuilder(rbb.conf, () => f)

    def future[A](f: => Future[A]): FutureFailableRetryBuilder[A] = new FutureFailableRetryBuilder(rbb.conf, () => f)

    def futureEither[E, S](f: => Future[Either[E, S]]): FutureEitherRetryBuilder[E, S] = new FutureEitherRetryBuilder(rbb.conf, () => f)
  }

  /** Operations on RetryBuilder */
  implicit class RetryBuilderOps[R <: RetryBuilder[_, _]](retryBuilder: R) {
    def toFuture[A](implicit typeclass: R >>> Future[A]): Future[A] = typeclass.convert(retryBuilder)
  }
}
