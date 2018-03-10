package quixote.retry

import quixote.configuration.Configuration

/** Start building a Retry: only the configuration is known right now */
class RetryBuilderBase(val conf: Configuration)

object RetryBuilderBase {

  val default = new RetryBuilderBase(Configuration.default)

  def named(name: String) = new RetryBuilderBase(Configuration.named(name))

  def custom(config: Configuration) = new RetryBuilderBase(config)
}