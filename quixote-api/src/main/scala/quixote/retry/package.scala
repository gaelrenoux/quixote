package quixote

import quixote.configuration.Configuration

package object retry {

  type >>>[R <: RetryBuilder[_, _], T] = ConvertsTo[R, T]

  def retry: RetryBuilderBase = RetryBuilderBase.default

  def retry(name: String): RetryBuilderBase = RetryBuilderBase.named(name)

  def retry(config: Configuration): RetryBuilderBase = RetryBuilderBase.custom(config)
}
