package quixote.configuration

import com.typesafe.config.{Config, ConfigException, ConfigFactory}
import quixote.util.JavaScalaConversions._

import scala.concurrent.duration.{Duration, FiniteDuration}

case class Configuration(
                          timeout: FiniteDuration,
                          maxAttempts: Long,
                          retryDelay: FiniteDuration,
                          retryDelayMultipler: Double,
                          retryDelayMax: Duration,
                          cache: CacheConfiguration
                        )

case class CacheConfiguration(

                               shortTimeout: Option[FiniteDuration],
                               expiration: FiniteDuration,
                               absoluteExpiration: Duration,
                               cacheSize: Option[Int],
                               autoRefreshPeriodMultiplier: Option[Double]
                             )


object Configuration {
  private val typesafeConfiguration = ConfigFactory.load().getConfig("quixote")

  private val defaultConf = typesafeConfiguration.getConfig("default")

  def named(name: String): Configuration = {
    val namedConfigOption =
      try Some(typesafeConfiguration.getConfig(name)) catch {
        case _: ConfigException.Missing => None
      }

    /* Get from the named configuration if it exists, of from the default if it doesn't */
    def getOrDefault[A](key: String)(fun: Config => String => A): A = namedConfigOption match {
      case Some(c) if c.hasPathOrNull(key) => fun(c)(key)
      case _ => fun(defaultConf)(key)
    }

    Configuration(
      timeout = getOrDefault[java.time.Duration]("timeout")(_ getDuration),
      maxAttempts = getOrDefault[Long]("max-attempts")(_ getLong),
      retryDelay = getOrDefault[java.time.Duration]("retry-delay")(_ getDuration),
      retryDelayMultipler = getOrDefault("retry-delay-multiplier")(_ getDouble),
      retryDelayMax = getOrDefault("retry-delay-max") { c => k => if (c.getIsNull(k)) Duration.Inf else c.getDuration(k) },
      cache = CacheConfiguration(
        shortTimeout = getOrDefault("cache.short-timeout") { c => k => if (c.getIsNull(k)) None else Some(c.getDuration(k)) },
        expiration = getOrDefault[java.time.Duration]("cache.expiration")(_ getDuration),
        absoluteExpiration = getOrDefault("cache.absolute-expiration") { c => k => if (c.getIsNull(k)) Duration.Inf else c.getDuration(k) },
        cacheSize = getOrDefault("cache.max-size") { c => k => if (c.getIsNull(k)) None else Some(c.getInt(k)) },
        autoRefreshPeriodMultiplier = getOrDefault("cache.auto-refresh-period-multiplier") { c => k => if (c.getIsNull(k)) None else Some(c.getDouble(k)) }
      )
    )
  }

  val default: Configuration = named("default")

}

