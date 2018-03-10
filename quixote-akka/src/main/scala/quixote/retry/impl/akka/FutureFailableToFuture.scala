package quixote.retry.impl.akka

import akka.actor.ActorRefFactory
import quixote.retry._

import scala.concurrent.{ExecutionContext, Future}

class FutureFailableToFuture[S](implicit val arf: ActorRefFactory, ec: ExecutionContext)
  extends ConvertsTo[FutureFailableRetryBuilder[S], Future[S]] {

  val wrapped = new FutureEitherToFuture[Throwable, S]

  override def convert(rb: FutureFailableRetryBuilder[S]): Future[S] = {
    wrapped.convert(rb.toFutureEitherRetryBuilder) map {
      case Left(failure) => throw failure
      case Right(success) => success
    }
  }

}
