package quixote.cache

import scala.concurrent.Future

trait ResilientMapCache[-K, +V] {

  /** Return a cached value. Will fail if the cache cannot return any value. */
  def get(key: K): Future[CachedValue[V]]

}
