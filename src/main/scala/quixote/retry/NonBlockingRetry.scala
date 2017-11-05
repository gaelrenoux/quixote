package quixote.retry

import quixote.configuration.Configuration
import shapeless.HList

import scala.concurrent.Future
import scala.reflect.ClassTag

/**
  * Retry a process. Note that there is a default timeout of one hour.
  *
  * TODO should implement effect from Scalaz or Cats ?
  * @author Gaël Renoux
  */
case class NonBlockingRetry[+A: ClassTag] private(configuration: Configuration)(f: => Future[A])(retry: PartialFunction[Throwable, Unit]) {

  /** Runs the process. As the result is not cached, any further call to run will result in (again) multiple attempts. */
  def run(): Future[A] = ???

}

object NonBlockingRetry {

  /** Takes a block that produces a future, and returns the cache ready to use. */
  def default[A: ClassTag](f: => Future[A]) =
    new Builder[A](Configuration.default, f)

  /** Takes a block that produces a future, and returns the cache ready to use. */
  def named[A: ClassTag](name: String)(f: => Future[A]) =
    new Builder[A](Configuration.named(name), f)

  /** Takes a block that produces a future, and returns the cache ready to use. */
  def custom[A: ClassTag](config: Configuration)(f: => Future[A]) =
    new Builder[A](config, f)

  class Builder[+A: ClassTag](config: Configuration, f: => Future[A]) {

    def retry(pf: PartialFunction[Throwable, Unit]) = new NonBlockingRetry[A](config)(f)(pf)

    def retryOn[B <: Throwable](implicit tag: ClassTag[B]): NonBlockingRetry[A] = retry {
      case tag(_) =>
    }

    def retryOnAll[B <: HList](implicit tag: ClassTag[B]): NonBlockingRetry[A] = retry {
      case _ => ???
    }
  }

}

