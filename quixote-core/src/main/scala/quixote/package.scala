import quixote.configuration.Configuration
import quixote.retry.RetryBuilder

package object quixote {

  def retry = RetryBuilder.default

  def retry(name: String) = RetryBuilder.named(name)

  def retry(config: Configuration) = RetryBuilder.custom(config)
}
