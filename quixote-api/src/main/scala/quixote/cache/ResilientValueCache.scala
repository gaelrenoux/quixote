package quixote.cache

import quixote.configuration.Configuration

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag


class ResilientValueCache[+A: ClassTag] private(configuration: Configuration)(f: => Future[A]) {

  /** Return a cached value. Will fail if the cache cannot return any value. */
  def get(implicit ec: ExecutionContext): Future[CachedValue[A]] = {
    if (configuration.maxAttempts > 0) f.map(LiveValue(_))
    else f.map(ExpiredValue(_, new UnsupportedOperationException))
  }



}

object ResilientValueCache {

  /** Takes a block that produces a future, and returns the cache ready to use. */
  def default[A: ClassTag](f: => Future[A]) =
    new ResilientValueCache[A](Configuration.default)(f)

  /** Takes a block that produces a future, and returns the cache ready to use. */
  def named[A: ClassTag](name: String)(f: => Future[A]) =
    new ResilientValueCache[A](Configuration.named(name))(f)

  /** Takes a block that produces a future, and returns the cache ready to use. */
  def custom[A: ClassTag](config: Configuration)(f: => Future[A]) =
    new ResilientValueCache[A](config)(f)

}
