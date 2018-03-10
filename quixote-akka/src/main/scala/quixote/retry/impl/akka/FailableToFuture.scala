package quixote.retry.impl.akka

import akka.actor.ActorRefFactory
import quixote.retry._

import scala.concurrent.{ExecutionContext, Future}

class FailableToFuture[S](implicit val arf: ActorRefFactory, ec: ExecutionContext)
  extends ConvertsTo[FailableRetryBuilder[S], Future[S]] {

  val wrapped = new EitherToFuture[Throwable, S]

  override def convert(rb: FailableRetryBuilder[S]): Future[S] = {
    wrapped.convert(rb.toEitherRetryBuilder) map {
      case Left(failure) => throw failure
      case Right(success) => success
    }
  }

}
