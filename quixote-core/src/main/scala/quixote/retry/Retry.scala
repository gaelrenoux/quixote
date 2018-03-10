package quixote.retry

import quixote.configuration.Configuration
import shapeless.HList

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

/**
  * Retry a process.
  * @tparam A is the success value type
  * @tparam B is the error value type
  *
  * @author Gaël Renoux
  */
abstract class Retry[+A, +B] {

  def noRetry(f: PartialFunction[B, Unit]): Retry[A, B]

}

