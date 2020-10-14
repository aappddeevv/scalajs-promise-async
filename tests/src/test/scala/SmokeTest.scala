// similar to https://github.com/scala/scala-async/blob/master/src/test/scala/scala/async/SmokeTest.scala
package ttg.scalajs.async

import utest._

import scala.scalajs.js
import ttg.scalajs.async.Async._
import ttg.scalajs.async.OptionAwait._

object SmokeTest extends TestSuite {
  val tests = Tests {
    test("js.Promise 1") {
      val result = async {
        await(js.Promise.resolve[Int](1)) + await(js.Promise.resolve[Int](2))
      }
      result
        .`then`[Unit](
          { result: Int =>
            println("Macro success, running assert"); assert(result == 3)
          },
          js.defined { (err: Any) => println(s"Error $err") }
        )
        .toFuture
    }
  }
}

object SmokeTestOpt extends TestSuite {
  val tests = Tests {
    test("opt test") {
      val x = optionally {
        value(Option(1)) + value(Option(2))
      }
      assert(x == Option(3))
    }
  }
}
