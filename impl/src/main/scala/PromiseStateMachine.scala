package ttg.scalajs.async

import scala.scalajs.js
import js.|

abstract class PromiseStateMachine() extends (js.Thenable[AnyRef] => Unit) {

  type RVAL[A] = A | js.Thenable[A]
  type RESOLVE[A, B] = js.Function1[A, RVAL[B]]
  type REJECTED[A] = js.Function1[scala.Any, RVAL[A]]

  // we can cheat quite abit on javascript platform, single threaded
  // breaking out the callbacks essentially creates a "defered promise" => scala Promise
  private[this] var succeed: AnyRef => Unit = null
  private[this] var fail: AnyRef => Unit = null
  // value and error channels are broken out apart and broken out of effect
  private[this] var resultValue: AnyRef = null
  private[this] var errorValue: AnyRef = null
  private[this] val result$async: js.Promise[AnyRef] = new js.Promise[AnyRef]({
    (s, f) =>
      println(">result$async init")
      succeed = (result: AnyRef) => {
        resultValue = result
        errorValue = null
        s(result.asInstanceOf[RVAL[AnyRef]])
      }
      fail = (e: AnyRef) => {
        resultValue = null
        errorValue = e
        f(e.asInstanceOf[Any])
      }
  })

  private[this] var state$async: Int = 0
  protected def state: Int = state$async
  protected def state_=(s: Int): Unit = state$async = s

  // FSM translated method
  def apply(tr$async: js.Thenable[AnyRef]): Unit

  protected def completeFailure(t: Throwable): Unit = {
    println(">completeFailure")
    // unwrap?
    fail(t)
    //throw t
  }
  protected def completeSuccess(value: AnyRef): Unit = {
    println(">completeSuccess")
    succeed(value)
  }
  protected def onComplete(f: js.Thenable[AnyRef]): Unit = {
    println(">onCompleted called")
    val resolve: RESOLVE[AnyRef, AnyRef] = (v: AnyRef) => succeed(v)
    val reject: REJECTED[AnyRef] = (err: Any) => {
      fail(err.asInstanceOf[AnyRef]); err.asInstanceOf[AnyRef]
    }
    f.`then`(resolve, js.defined(reject))
  }
  protected def getCompleted(f: js.Promise[AnyRef]) = {
    println(">getCompleted")
    null
    //f
  }
  protected def tryGet(tr: js.Promise[AnyRef]): AnyRef = {
    println(">tryGet")
    tr
    //this
  }

  def start[T](): js.Promise[T] = {
    println(">start called")
    // No real action here as the promise, `result$async`, starts eagerly on its own
    // inside this class but waits on the completion callbacks to be called.
    result$async.asInstanceOf[js.Promise[T]]
  }
}
