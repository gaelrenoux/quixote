package quixote.cache

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import quixote.configuration.Configuration

import scala.concurrent.Future

/**
  * @author Gaël Renoux
  */
class TriggeredRvcTest extends FlatSpec with Matchers with BeforeAndAfterAll with ScalaFutures {

  behavior of "Builder"

  it should "create a cache with default configuration" in {

    val r1 = ResilientValueCache.default {
      Future.successful(0)
    }
  }

  it should "create a cache with named configuration" in {

    val r = ResilientValueCache.named("test") {
      Future.successful(0)
    }
  }

  it should "create a cache with custom configuration" in {

    val r = ResilientValueCache.custom(Configuration.named("test")) {
      Future.successful(0)
    }
  }

}

