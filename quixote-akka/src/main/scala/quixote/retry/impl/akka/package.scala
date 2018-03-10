package quixote.retry.impl

import _root_.akka.actor.ActorRefFactory

import scala.concurrent.ExecutionContext

/** Implementation of retry based on Akka actors */
package object akka {

  /** Conversion from a FailableRetryBuilder, to a Future */
  implicit def failableToFuture[A](implicit arf: ActorRefFactory, ec: ExecutionContext): akka.FailableToFuture[A] =
    new akka.FailableToFuture[A]

  /** Conversion from a EitherRetryBuilder, to a Future[Either] */
  implicit def eitherToFutureEither[E, S](implicit arf: ActorRefFactory, ec: ExecutionContext): akka.EitherToFuture[E, S] =
    new akka.EitherToFuture[E, S]

  /** Conversion from a FutureFailableRetryBuilder, to a Future */
  implicit def futureFailableToFuture[A](implicit arf: ActorRefFactory, ec: ExecutionContext): akka.FutureFailableToFuture[A] =
    new akka.FutureFailableToFuture[A]

  /** Conversion from a FutureEitherRetryBuilder, to a Future[Either] */
  implicit def futureEitherToFutureEither[E, S](implicit arf: ActorRefFactory, ec: ExecutionContext): akka.FutureEitherToFuture[E, S] =
    new akka.FutureEitherToFuture[E, S]

}
