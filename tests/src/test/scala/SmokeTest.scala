// similar to https://github.com/scala/scala-async/blob/master/src/test/scala/scala/async/SmokeTest.scala
package ttg.scalajs.async

import utest._

import scala.scalajs.js
import ttg.scalajs.async.Async._

object SmokeTest extends TestSuite {
  val tests = Tests {
    test("smoketest1") {
      val result = async {
        await(js.Promise.resolve[Int](1)) + await(js.Promise.resolve[Int](2))
      }
      result.`then`[Unit]({ result: Int => assert(result == 3) }).toFuture
    }
  }
}
