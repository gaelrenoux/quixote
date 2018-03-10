package quixote.retry

/**
  * Builder for a Retry. The operation has been set.
  * @tparam E is the error value type
  * @tparam S is the success value type
  */
trait RetryBuilder[+E, +S] {

  def noRetry(f: PartialFunction[E, Unit]): RetryBuilder[E, S]

}

