package quixote.retry

import quixote.configuration.Configuration

import scala.concurrent.Future

class RetryBuilder(configuration: Configuration) {

  def future[A](f: => Future[A]): Retry[A, Throwable] = ???

  def either[A, B](e: => Either[A, B]): Retry[A, B] = ???


}

object RetryBuilder {

  val default = new RetryBuilder(Configuration.default)

  def named(name: String) = new RetryBuilder(Configuration.named(name))

  def custom(config: Configuration) = new RetryBuilder(config)
}