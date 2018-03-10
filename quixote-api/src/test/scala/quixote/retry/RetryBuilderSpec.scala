package quixote.retry

import com.typesafe.scalalogging.Logger
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import quixote.configuration.Configuration
import quixote.retry.impl.noRetry._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * @author Gaël Renoux
  */
class RetryBuilderSpec extends FlatSpec with Matchers with BeforeAndAfterEach with BeforeAndAfterAll with ScalaFutures with EitherValues {

  private val log = Logger[RetryBuilderSpec]

  case class MyTestException(i: Int) extends Exception

  "All methods to get a configuration" should "compile" in {

    val a: Future[Int] = retry.future {
      Future(if (true) 42 else throw new IllegalArgumentException)
    } toFuture

    val b: Future[Int] = retry("testConfig").future {
      Future(if (true) 42 else throw new IllegalArgumentException)
    } toFuture

    val c: Future[Int] = retry(Configuration.default).future {
      Future(if (true) 42 else throw new IllegalArgumentException)
    } toFuture
  }

  "retry.failable" should "compile" in {

    val a: Future[Int] = retry("testConfig").failable {
      if (true) 42 else throw new IllegalArgumentException
    } toFuture

    val z: Future[Int] = retry("testConfig").failable {
      if (true) 42 else throw new IllegalArgumentException
    } noRetry {
      case _: IllegalArgumentException => log.debug("toto")
    } toFuture

  }

  "retry.tried" should "compile" in {

    val a: Future[Int] = retry("testConfig").tried {
      if (true) Success(42) else Failure(new IllegalArgumentException)
    } toFuture

    val z: Future[Int] = retry("testConfig").tried {
      if (true) Success(42) else Failure(new IllegalArgumentException)
    } noRetry {
      case _: IllegalArgumentException => log.debug("toto")
    } toFuture

  }

  "retry.either" should "compile" in {

    val a: Future[Either[String, Int]] = retry("testConfig").either {
      if (true) Right(42) else Left("8x7")
    } toFuture

    val z: Future[Either[String, Int]] = retry("testConfig").either {
      if (true) Right(42) else Left("8x7")
    } noRetry {
      case "56" => log.debug("toto")
    } toFuture

  }

  "retry.future" should "compile" in {

    val a: Future[Int] = retry("testConfig").future {
      Future(if (true) 42 else throw new IllegalArgumentException)
    } toFuture

    val b: Future[Either[String, Int]] = retry("testConfig").future {
      Future(if (true) Right(42) else Left("8x7"))
    } toFuture

    val z: Future[Int] = retry("testConfig").future {
      Future(if (true) 42 else throw new IllegalArgumentException)
    } noRetry {
      case _: IllegalArgumentException => log.debug("toto")
    } toFuture

  }

  "retry.futureEither" should "compile" in {

    val a: Future[Either[String, Int]] = retry("testConfig").futureEither {
      if (true) Future(Right(42)) else Future(Left("8x7"))
    } toFuture

    val z: Future[Either[String, Int]] = retry("testConfig").futureEither {
      if (true) Future(Right(42)) else Future(Left("8x7"))
    } noRetry {
      case "56" => log.debug("toto")
    } toFuture

  }

}

