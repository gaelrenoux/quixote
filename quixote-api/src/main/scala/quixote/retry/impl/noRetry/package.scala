package quixote.retry.impl

/** Very basic implementation: no retry at all ! */
package object noRetry {

  /** Conversion from a FailableRetryBuilder, to a Future */
  implicit def failableToFuture[A]: FailableToFuture[A] = new FailableToFuture[A]

  /** Conversion from a EitherRetryBuilder, to a Future[Either] */
  implicit def eitherToFutureEither[E, S]: EitherToFuture[E, S] = new EitherToFuture[E, S]

  /** Conversion from a FutureFailableRetryBuilder, to a Future */
  implicit def futureFailableToFuture[A]: FutureFailableToFuture[A] = new FutureFailableToFuture[A]

  /** Conversion from a FutureEitherRetryBuilder, to a Future[Either] */
  implicit def futureEitherToFutureEither[E, S]: FutureEitherToFuture[E, S] = new FutureEitherToFuture[E, S]

}
