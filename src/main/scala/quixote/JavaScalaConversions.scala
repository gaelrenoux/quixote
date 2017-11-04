package quixote

/**
  * @author Gaël Renoux
  */
private[quixote] object JavaScalaConversions {

  /** Converts a Java Duration, which is finite, into a Scala FiniteDuration */
  implicit def asScalaFiniteDuration(d: java.time.Duration): scala.concurrent.duration.FiniteDuration =
    scala.concurrent.duration.Duration.fromNanos(d.toNanos)

  /** Converts a Scala FiniteDuration into a Java Duration, which is finite */
  implicit def asJavaDuration(d: scala.concurrent.duration.FiniteDuration): java.time.Duration =
    java.time.Duration.ofNanos(d.toNanos)

}
