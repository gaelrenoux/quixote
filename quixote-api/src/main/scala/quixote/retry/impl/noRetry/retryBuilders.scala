package quixote.retry.impl.noRetry

import quixote.retry.RetryBuilder

import scala.concurrent.Future

class FailableRetryBuilder[+S](val f: () => S) extends RetryBuilder[Throwable, S] {
  override def noRetry(f: PartialFunction[Throwable, Unit]): FailableRetryBuilder[S] = this
}

class EitherRetryBuilder[+E, +S](val f: () => Either[E, S]) extends RetryBuilder[E, S] {
  override def noRetry(f: PartialFunction[E, Unit]): EitherRetryBuilder[E, S] = this
}

class FutureFailableRetryBuilder[+S](val f: () => Future[S]) extends RetryBuilder[Throwable, S] {
  override def noRetry(f: PartialFunction[Throwable, Unit]): FutureFailableRetryBuilder[S] = this
}

class FutureEitherRetryBuilder[+E, +S](val f: () => Future[Either[E, S]]) extends RetryBuilder[E, S] {
  override def noRetry(f: PartialFunction[E, Unit]): FutureEitherRetryBuilder[E, S] = this
}

