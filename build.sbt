Global / onChangedBuildSource := ReloadOnSourceChanges

inThisBuild(
  List(
    scalaVersion := "2.13.3",
    organization := "ttg",
    organizationName := "The Trapelo Group",
    version := "0.1.0",
    description := "-Xasync for scala.js for javascript Promises."
  )
)

lazy val root = project
  .in(file("."))
  .aggregate(impl, tests)
  .settings(skip in publish := true)

lazy val tests = project
  .settings(
    scalacOptions in Test ++= Seq("-Yrangepos"),
    scalacOptions ++= List("-deprecation", "-Xasync"),
    // https://github.com/lihaoyi/utest
    libraryDependencies += "com.lihaoyi" %%% "utest" % "0.7.5" % "test",
    testFrameworks += new TestFramework("utest.runner.Framework")
  )
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(impl)

lazy val impl = project
  .settings(
    name := "scalajs-promise-async",
    scalacOptions ++= Seq("-language:experimental.macros"),
    libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
  )
  .enablePlugins(ScalaJSPlugin)
