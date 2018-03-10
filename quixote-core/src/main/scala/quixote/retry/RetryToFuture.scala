package quixote.retry

import scala.concurrent.Future

/** Typeclass */
trait RetryToFuture[R[_, _] <: Retry[_, _]] {

  def run[A, B, C](retry: R[A, B]): Future[C]

}
