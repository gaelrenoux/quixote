package quixote.retry

import quixote.configuration.Configuration

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class FutureFailableRetryBuilder[+S](
                                      val conf: Configuration,
                                      val f: () => Future[S],
                                      val noRetryCases: PartialFunction[Throwable, Unit] = PartialFunction.empty
                                    ) extends RetryBuilder[Throwable, S] {

  override def noRetry(newCases: PartialFunction[Throwable, Unit]) =
    new FutureFailableRetryBuilder(conf, f, newCases.orElse(noRetryCases))

  /** Conversion to a RetryBuilder for a Future returning an either */
  private[quixote] def toFutureEitherRetryBuilder(implicit ec: ExecutionContext) =
    new FutureEitherRetryBuilder[Throwable, S](
      conf,
      () => f() map (Right(_)) recover { case NonFatal(ex) => Left(ex) },
      noRetryCases
    )
}

