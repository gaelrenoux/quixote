package quixote.cache

/** A cached value. It can either be live (within the expiration period) or expired (expiration period is over,
  *  but it was still returned for lack of a live value). */
sealed trait CachedValue[+A] {
  val value: A
}

/** Cached value that was within the expiration period when it was returned. */
case class LiveValue[+A](value: A) extends CachedValue[A]

/** Cached value that was out of the expiration period, with the reason for it (for logging purposes). */
case class ExpiredValue[+A](value: A, cause: Throwable) extends CachedValue[A]
