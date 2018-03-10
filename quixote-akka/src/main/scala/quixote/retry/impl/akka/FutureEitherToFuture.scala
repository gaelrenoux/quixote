package quixote.retry.impl.akka

import akka.actor.ActorRefFactory
import akka.util.Timeout
import quixote.retry._
import quixote.retry.impl.akka.actors.RetryingActor
import quixote.retry.impl.akka.actors.RetryingActor.Messages._

import scala.concurrent.{ExecutionContext, Future}

class FutureEitherToFuture[E, S](implicit val arf: ActorRefFactory, ec: ExecutionContext)
  extends ConvertsTo[FutureEitherRetryBuilder[E, S], Future[Either[E, S]]] {

  override def convert(rb: FutureEitherRetryBuilder[E, S]): Future[Either[E, S]] = {

    /* No-retry cases converted into a Right, to avoid the retry */
    def correctedF(): Future[Either[E, Either[E, S]]] = rb.f() map {
      case Left(x) if rb.noRetryCases.isDefinedAt(x) =>
        rb.noRetryCases(x) // execute, there might be a log or something
        Right(Left(x))
      case Left(x) => Left(x)
      case r => Right(r) // Right of Right
    }

    val actorRef = arf.actorOf(RetryingActor.props[E, Either[E, S]](rb.conf, () => correctedF()))

    import akka.pattern.ask
    implicit val askTimeout: Timeout = Timeout(rb.conf.timeout * 2) //timeout handled by the actor

    /* Get avoided retries back into Lefts */
    actorRef.ask(Start).mapTo[RetryResult[E, Either[E, S]]] map {
      case RetrySuccessful(either) => either
      case RetryFailed(e) => Left(e)
    }
  }




}
