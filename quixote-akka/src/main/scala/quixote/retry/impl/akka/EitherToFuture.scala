package quixote.retry.impl.akka

import akka.actor.ActorRefFactory
import quixote.retry._

import scala.concurrent.{ExecutionContext, Future}

class EitherToFuture[E, S](implicit val arf: ActorRefFactory, ec: ExecutionContext)
  extends ConvertsTo[EitherRetryBuilder[E, S], Future[Either[E, S]]] {

  val wrapped = new FutureEitherToFuture[E, S]

  override def convert(rb: EitherRetryBuilder[E, S]): Future[Either[E, S]] = {
    val futureRb = new FutureEitherRetryBuilder[E, S](
      rb.conf,
      () => Future(rb.f()),
      rb.noRetryCases
    )
    wrapped.convert(futureRb)
  }

}
