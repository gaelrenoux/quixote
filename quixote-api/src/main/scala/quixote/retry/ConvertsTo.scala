package quixote.retry

/** Typeclass: define a possibility to convert from a type of RetryBuilder, to a target type */
trait ConvertsTo[R <: RetryBuilder[_, _], T] {
  def convert(rb: R): T
}