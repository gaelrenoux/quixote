package quixote.retry.impl.noRetry


import quixote.retry.ConvertsTo
import quixote.retry._

import scala.concurrent.Future

class FailableToFuture[S] extends ConvertsTo[FailableRetryBuilder[S], Future[S]] {
  override def convert(rb: FailableRetryBuilder[S]): Future[S] = Future.successful(rb.f())
}

class EitherToFuture[E, S] extends ConvertsTo[EitherRetryBuilder[E, S], Future[Either[E, S]]] {
  override def convert(rb: EitherRetryBuilder[E, S]): Future[Either[E, S]] = Future.successful(rb.f())
}

class FutureFailableToFuture[S] extends ConvertsTo[FutureFailableRetryBuilder[S], Future[S]] {
  override def convert(rb: FutureFailableRetryBuilder[S]) = rb.f()
}

class FutureEitherToFuture[E, S] extends ConvertsTo[FutureEitherRetryBuilder[E, S], Future[Either[E, S]]] {
  override def convert(rb: FutureEitherRetryBuilder[E, S]) = rb.f()
}
