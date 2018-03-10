package quixote.retry

import quixote.configuration.Configuration


class EitherRetryBuilder[E, +S](
                                  val conf: Configuration,
                                  val f: () => Either[E, S],
                                  val noRetryCases: PartialFunction[E, Unit] = PartialFunction.empty
                                ) extends RetryBuilder[E, S] {

  override def noRetry(newCases: PartialFunction[E, Unit]) =
    new EitherRetryBuilder(conf, f, newCases.orElse(noRetryCases))
}
