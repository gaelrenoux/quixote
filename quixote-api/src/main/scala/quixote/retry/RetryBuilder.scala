package quixote.retry

import quixote.configuration.Configuration

/**
  * Builder for a Retry. The operation has been set.
  *
  * @tparam E is the error value type
  * @tparam S is the success value type
  */
trait RetryBuilder[E, +S] {

  val conf: Configuration
  val noRetryCases: PartialFunction[E, Unit]

  def noRetry(cases: PartialFunction[E, Unit]): RetryBuilder[E, S]

}

