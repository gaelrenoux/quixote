package quixote.retry

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import quixote._
import quixote.configuration.Configuration

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * @author Gaël Renoux
  */
class RetrySpec extends FlatSpec with Matchers with BeforeAndAfterEach with BeforeAndAfterAll with ScalaFutures with EitherValues {

  private val DelayMs = 249L

  private val StandardWait = 250L

  private def delayed(durationMs: Long) = durationMs + DelayMs

  case class MyTestException(i: Int) extends Exception

  implicit val patience = PatienceConfig(timeout = 1 minute, interval = 100 milliseconds)


  behavior of "retry"

  it should "work" in {
    val _: Future[Int] = retry.future {
      Future(if (true) 42 else throw new IllegalArgumentException)
    } toFuture

    val _: Future[Int] = retry("testConfig").future {
      Future(if (true) 42 else throw new IllegalArgumentException)
    } toFuture

    val _: Future[Int] = retry(Configuration.default).future {
      Future(if (true) 42 else throw new IllegalArgumentException
    } toFuture

    val _: Future[Either[String, Long]] = retry("testConfig").either {
      if (true) Right(42) else Left("8x7")
    } toFuture

    val _: Future[Either[String, Long]] = retry("testConfig").futureEither {
      if (true) Future(Right(42)) else Future(Left("8x7"))
    } toFuture

    val _: Future[Int] = retry("testConfig").failable {
      if (true) 42 else throw new IllegalArgumentException
    } toFuture

    val _: Future[Int] = retry("testConfig").failable {
      if (true) Success(42) else Failure(IllegalArgumentException)
    } toFuture

    val _: Future[Int] = retry("testConfig").future {
      Future(if (true) 42 else throw new IllegalArgumentException
    } noRetry {
      case ex: IllegalArgumentException => log.debug("toto")
    } toFuture

    val _: Future[Either[String, Long]] = retry("testConfig").either {
      if (true) Right(42) else Left("8x7")
    } noRetry {
      case Left("56") => log.debug("toto")
    } toFuture

    val _: Future[Either[String, Long]] = retry("testConfig").futureEither {
      if (true) Future(Right(42)) else Future(Left("8x7"))
    } noRetry {
      case Left("56") => log.debug("toto")
    } toFuture




  }

}

