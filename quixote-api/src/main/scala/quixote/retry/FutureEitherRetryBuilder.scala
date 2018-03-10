package quixote.retry

import quixote.configuration.Configuration

import scala.concurrent.Future

class FutureEitherRetryBuilder[E, +S](
                                       val conf: Configuration,
                                       val f: () => Future[Either[E, S]],
                                       val noRetryCases: PartialFunction[E, Unit] = PartialFunction.empty
                                     ) extends RetryBuilder[E, S] {

  override def noRetry(newCases: PartialFunction[E, Unit]) =
    new FutureEitherRetryBuilder(conf, f, newCases.orElse(noRetryCases))
}


