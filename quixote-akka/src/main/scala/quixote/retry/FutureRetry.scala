package quixote.retry

import quixote.configuration.Configuration

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

case class FutureRetry[+A: ClassTag, +B: ClassTag](config: Configuration)(f: () => Future[A]) extends Retry[A, B] {

  def toFuture(implicit ec: ExecutionContext): Future[A] = ???

  def unsafeSync(): A = ???

}
