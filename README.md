# scalajs-promise-async

An implementation for scalajs `js.Thenable` that conforms to the async API expected by the scala compiler.

## Quick start

To include scalajs-promise-async in an existing project use the library published on Maven Central.
For sbt projects add the following to your build definition - build.sbt or project/Build.scala:

### Use a modern Scala compiler

As of scala-async 1.0, Scala 2.12.12+ or 2.13.3+ are required.

### Add dependency

#### SBT Example

```scala
libraryDependencies += "ttg.scalajs" %%% "scalajs-promise-async" % "0.1.0"
libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided
```
### Enable compiler support for `async`

Add the `-Xasync` to the Scala compiler options.

#### SBT Example
```scala
scalacOptions += "-Xasync"
```

### Start Coding

```scala
// ...
```

## Basic Idea

The scala compiler flag `-Xasync` enables a specific transform that depends on an API. Assuming you use a macro to
code generate content that confirms to this API, the scala compiler will handle the transform and leveraging
your async implementation. 


## Background

* Future: https://github.com/scala/scala-async
* scala compiler PR: https://github.com/scala/scala/pull/8816
* scala compiler Option test: https://github.com/scala/scala/blob/0bf974149c1a83e62cc6a3550c996872ae3836cb/src/partest/scala/tools/partest/async/OptionDsl.scala
* scala compiler misc test: https://github.com/scala/scala/blob/0bf974149c1a83e62cc6a3550c996872ae3836cb/src/partest/scala/tools/partest/async/OutputAwait.scala
* scala compiler CompletableFuture test: https://github.com/scala/scala/blob/v2.13.3/src/partest/scala/tools/partest/async/CompletableFutureAwait.scala
* discussion: https://github.com/scala/scala-async/issues/149
* old twitter version (not relevant to new API): https://github.com/foursquare/twitter-util-async
* retronym's test bed: https://github.com/retronym/monad-ui/tree/master/src/main/scala/monadui
  * Has monix version
* graalvm yona, has this builtin (dedicated language for effects): https://github.com/yona-lang/yona

## Help from scala contributors gitter


```
 October 13, 2020 3:04 AM @aappddeevv getCompleted is optional, the implementation can choose to continue immediately for an already-completed future. You can return null to indicate that the FSM should instead register itself as a completion handler and wait for the callback. tryGet, for scala.Future converts the Success(x) to x. for Failure(ex) it completes the Future with the exception and aborts the FSM.
The exception wrapping type (and whether there is one at all) can vary between future impls.
So hopefully this is enough to use -Xasync for Scala.js -- you definitely don't need to be able to block.
Please let me know how it goes.

Seth Tisue @SethTisue Oct 13 02:28
@aappddeevv you might mention on scala/scala-async#248 that you're interested/working on this

aappddeevv @aappddeevv Oct 13 10:15
@retronym @SethTisue That's new information to me about the build. I don't see any need to block. But let's say getCompleted returns null, I can't do a tryGet at all--there's no synchronous peeking into a vanilla js.Promise for anything close to a tryGet. That still leaves me with 1 required API call that I cannot satisfy on a vanilla js.Promise.

Jason Zaugg @retronym Oct 13 19:34
@aappddeevv tryGet takes whatever the callback receives and extracts the successful value. That might just be an identity functiuon for js.Promise. For scala.concurrent.Future, we need to get from Try[T] => T.
```