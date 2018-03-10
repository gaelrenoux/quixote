package quixote.retry

import _root_.akka.actor.ActorSystem
import com.typesafe.scalalogging.Logger
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import quixote.configuration.Configuration
import quixote.retry.impl.akka._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

class AkkaRetrySpec extends FlatSpec with Matchers with BeforeAndAfterEach with BeforeAndAfterAll with ScalaFutures with EitherValues {

  implicit val as = ActorSystem("NonBlockingRetrySpec-as")

  case class MyTestException(i: Int) extends Exception

  implicit val patience = PatienceConfig(timeout = 1 minute, interval = 100 milliseconds)

  private val log = Logger[RetryBuilderSpec]


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

  behavior of "retry.futureEither"
  it should "succeeds on the n-th try" in {

    val n = 3
    var (attempts, lefts, rights) = (1, 0, 0)
    val f: Future[Either[String, Int]] = retry("no-delay").futureEither {
      Future {
        if (attempts == n) {
          rights += 1
          Right(42)
        } else {
          attempts += 1
          lefts += 1
          Left("8x7")
        }
      }
    } toFuture

    whenReady(f) { result =>
      result should be(Right(42))
      attempts should be(n)
      lefts should be(n - 1)
      rights should be(1)
    }
  }


}

