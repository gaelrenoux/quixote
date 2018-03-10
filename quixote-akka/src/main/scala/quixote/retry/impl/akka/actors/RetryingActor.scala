package quixote.retry.impl.akka.actors

import akka.actor.{Actor, ActorRef, Cancellable, Props}
import com.typesafe.scalalogging.Logger
import quixote.configuration.Configuration

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal


/**
  * @param conf Quixote configuration for this actor
  * @param f      Produces the Future completing with an Either. The Future may fail or throw an exception, in which case we
  *               return immediately.
  * @tparam E Failure type.
  * @tparam S Success type.
  */
class RetryingActor[E, S](
                           val conf: Configuration,
                           val f: () => Future[Either[E, S]]
                         ) extends Actor {

  import InternalMessages._
  import RetryingActor.Messages._
  import RetryingActor._

  private implicit val executionContext: ExecutionContext = context.system.dispatcher

  private val log = Logger[RetryingActor[_, _]]


  /** This is the initial receive of the actor. When it receives the `Start` message, it becomes `ready` with the
    * initial state.
    */
  final def receive: Receive = {
    case Start =>
      self ! Tick
      val scheduledTimeout = context.system.scheduler.scheduleOnce(conf.timeout, self, Timeout)

      context.become(ready(State[E, S](
        caller = sender(),
        nextDelay = conf.retryDelay,
        attemptsLeft = conf.maxAttempts,
        lastFailure = None,
        scheduledTimeout = scheduledTimeout
      )))

    case other => log.warn(s"Unexpected message $other in receive in actor $self")
  }

  /** Handle ticks and timeouts. When it receives a tick, it becomes `busy`. */
  private def ready(state: State[E, S]): Receive = {
    case Tick =>
      context.become(busy(state))
      executeAttempt()

    case Timeout =>
      onTimeout(state)

    case other =>
      log.warn(s"Unexpected message $other in ready in actor $self")
  }

  /** When handling a tick: handle execution results and errors. */
  private def busy(state: State[E, S]): Receive = {
    case FailedAttempt(failure) =>
      onFailure(state, failure) match {
        case None => //do nothing, actor is terminating
        case Some(newState) => context.become(ready(newState))
      }

    case SuccessfulAttempt(success) =>
      onSuccess(state, success)

    case CrashedAttempt(ex) =>
      onCrash(state, ex)

    case other =>
      log.warn(s"Unexpected message $other in busy in actor $self")
  }

  private def executeAttempt(): Unit = {
    val future = try f() catch {
      case NonFatal(th) => Future.failed(th)
    }
    future foreach {
      case Right(success) => self ! SuccessfulAttempt(success)
      case Left(failure) => self ! FailedAttempt(failure)
    }
    future.failed foreach {
      case NonFatal(throwable) => self ! CrashedAttempt(throwable)
    }
  }

  /** The action has returned a failure. If possible, continue trying. If not, reply with the failure and terminate. */
  private def onFailure(state: State[E, S], failure: E): Option[State[E, S]] = {
    if (state.attemptsLeft <= 0) {
      log.debug(s"No attempt left, sending $failure from $self to ${state.caller}")
      state.caller ! TooManyAttempts(failure)
      terminate(state)
      None
    } else {
      val newState = state.copy(
        nextDelay = incrementDelay(state.nextDelay),
        attemptsLeft = state.attemptsLeft - 1,
        lastFailure = Some(failure)
      )
      log.debug(s"Next tick in ${newState.nextDelay} (${newState.attemptsLeft} attempts left) on actor $self")
      val _ = context.system.scheduler.scheduleOnce(newState.nextDelay, self, Tick)
      Some(newState)
    }
  }

  /** The action has returned a success. Reply with the success and terminate. */
  private def onSuccess(state: State[E, S], success: S): Unit = {
    log.debug(s"Not pending anymore, sending $success from $self to ${state.caller}")
    state.caller ! RetrySuccessful(success)
    terminate(state)
  }

  /** The action threw an exception. Send the error back and terminate. */
  private def onCrash(state: State[E, S], throwable: Throwable): Unit = {
    log.debug(s"Future crashed, sending $throwable from $self to ${state.caller}")
    state.caller ! akka.actor.Status.Failure(throwable)
    terminate(state)
  }

  /** Timeout reached. Reply with the last failure known and terminate. */
  private def onTimeout(state: State[E, S]): Unit = state.lastFailure match {
    case None =>
      /* Can't happen */
      log.error(s"Timeout reached in actor $this but no call to execute have been done !")
      state.caller ! akka.actor.Status.Failure(new IllegalStateException())
      throw new IllegalStateException()

    case Some(badResult) =>
      log.debug(s"Timeout reached, sending $badResult from $self to ${state.caller}")
      state.caller ! TimeoutReached(badResult)
      terminate(state)
  }

  /** Properly terminate that actor, tying up lose ends. */
  private def terminate(state: State[E, S]): Unit = {
    log.trace(s"Actor $self terminated")
    state.scheduledTimeout.cancel()
    context.stop(self)
  }

  private def incrementDelay(delay: FiniteDuration) = (delay * conf.retryDelayMultipler).min(conf.retryDelayMax).asInstanceOf[FiniteDuration]


  /** Messages used internally by the actor */
  private object InternalMessages {

    /** A tick triggers an attempt */
    object Tick

    /** Timeout is reached */
    object Timeout

    sealed trait AttemptResult

    /** An attempt finished successfully (ended with a Right) */
    case class SuccessfulAttempt(success: S) extends AttemptResult

    /** An attempt failed (ended with a Left) */
    case class FailedAttempt(failure: E) extends AttemptResult

    /** An attempt crashed (threw a non-fatal exception) */
    case class CrashedAttempt(throwable: Throwable) extends AttemptResult

  }

}

object RetryingActor {

  def props[E, S](conf: Configuration, f: () => Future[Either[E, S]]) =
    Props(classOf[RetryingActor[E, S]], conf, f)

  /** Current state of the actor */
  private case class State[+E, +S](
                                    /** Caller of the actor */
                                    caller: ActorRef,

                                    /** Delay before next try */
                                    nextDelay: FiniteDuration,

                                    /** Numbers of attemps left */
                                    attemptsLeft: Long,

                                    /** Last bad result found. */
                                    lastFailure: Option[E],

                                    /** Handle to the timeout schedule, so that I can cancel it and not have a dead
                                      * letter when the actor terminates */
                                    scheduledTimeout: Cancellable
                                  )

  /** Messages used to communicate with the actor (input and output). */
  object Messages {

    /** Send to start retrying */
    object Start

    /** Result of the retrying actor: either a successful attempt, or the last failed attempt. */
    sealed abstract class RetryResult[+E, +R]

    case class RetrySuccessful[+E, +R](result: R) extends RetryResult[E, R]

    sealed abstract class RetryFailed[+E, +R] extends RetryResult[E, R] {
      val result: E
    }

    object RetryFailed {
      /** Deconstructor allowing to use AttemptFailed in cases, without caring which failure it is. */
      def unapply[E, R](arg: RetryFailed[E, R]): Option[E] = Some(arg.result)
    }

    case class TooManyAttempts[+E, +R](result: E) extends RetryFailed[E, R]

    case class TimeoutReached[+E, +R](result: E) extends RetryFailed[E, R]

  }

}