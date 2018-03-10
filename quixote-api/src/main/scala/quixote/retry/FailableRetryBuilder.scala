package quixote.retry

import quixote.configuration.Configuration

import scala.util.control.NonFatal


class FailableRetryBuilder[+S](
                                val conf: Configuration,
                                val f: () => S,
                                val noRetryCases: PartialFunction[Throwable, Unit] = PartialFunction.empty
                              ) extends RetryBuilder[Throwable, S] {

  override def noRetry(newCases: PartialFunction[Throwable, Unit]) =
    new FailableRetryBuilder(conf, f, newCases.orElse(noRetryCases))

  /** Conversion to a RetryBuilder for an either */
  private[quixote] def toEitherRetryBuilder = new EitherRetryBuilder[Throwable, S](
    conf,
    () => try Right(f()) catch { case NonFatal(ex) => Left(ex) },
    noRetryCases
  )
}