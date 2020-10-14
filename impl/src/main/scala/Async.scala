package ttg.scalajs.async

import scala.scalajs.js
import scala.language.experimental.macros
import scala.annotation.compileTimeOnly
import scala.reflect.macros.whitebox

object Async {
  def async[T](body: T): js.Promise[T] = macro asyncImpl[T]

  @compileTimeOnly("[async] `await` must be enclosed in an `async` block")
  def await[T](awaitable: js.Thenable[T]): T = ???

  def asyncImpl[T: c.WeakTypeTag](
      c: whitebox.Context
  )(body: c.Tree): c.Tree = {
    import c.universe._
    if (!c.compilerSettings.contains("-Xasync")) {
      c.abort(
        c.macroApplication.pos,
        "The async requires the compiler option -Xasync (supported only by Scala 2.12.12+ / 2.13.3+)"
      )
    } else
      try {
        val awaitSym = typeOf[Async.type].decl(TermName("await"))
        def mark(t: DefDef): Tree = {
          import language.reflectiveCalls
          c.internal
            .asInstanceOf[{
                def markForAsyncTransform(
                    owner: Symbol,
                    method: DefDef,
                    awaitSymbol: Symbol,
                    config: Map[String, AnyRef]
                ): DefDef
              }
            ]
            .markForAsyncTransform(
              c.internal.enclosingOwner,
              t,
              awaitSym,
              Map.empty
            )
        }
        val name = TypeName("stateMachine$async")
        q"""
      final class $name extends _root_.ttg.scalajs.async.PromiseStateMachine() {
        // FSM translated method
        ${mark(
          q"""override def apply(tr$$async: _root_.scala.scalajs.js.Thenable[_root_.scala.AnyRef]) = ${body}"""
        )}
      }
      new $name().start() : ${c.macroApplication.tpe}
    """
      } catch {
        case e: ReflectiveOperationException =>
          c.abort(
            c.macroApplication.pos,
            "-Xasync is provided as a Scala compiler option, but the async macro is unable to call c.internal.markForAsyncTransform. " + e.getClass.getName + " " + e.getMessage
          )
      }
  }
}
