package quixote.retry

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import quixote.configuration.Configuration
import shapeless.{HList, HNil}

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * @author Gaël Renoux
  */
class NonBlockingRetrySpec extends FlatSpec with Matchers with BeforeAndAfterEach with BeforeAndAfterAll with ScalaFutures with EitherValues {

  private val DelayMs = 249L

  private val StandardWait = 250L

  private def delayed(durationMs: Long) = durationMs + DelayMs

  case class MyTestException(i: Int) extends Exception

  implicit val patience = PatienceConfig(timeout = 1 minute, interval = 100 milliseconds)


  behavior of "Builder"

  it should "create a retry with default configuration" in {

    val r1 = NonBlockingRetry.default {
      Future.successful(0)
    } retry {
      case _ =>
    }
  }

  it should "create a retry with named configuration" in {

    val r = NonBlockingRetry.named("test") {
      Future.successful(0)
    }.retryOn[IllegalArgumentException]

  }

  it should "create a retry with custom configuration" in {

    val r = NonBlockingRetry.custom(Configuration.named("test")) {
      Future.successful(0)
    }.retryOnAll[IllegalArgumentException :: IllegalStateException :: HNil]
    //}.retryOnAll[HList.`IllegalArgumentException, IllegalStateException`.T]

  }

}

