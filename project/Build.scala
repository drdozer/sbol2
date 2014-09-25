import sbt._
import Keys._

import scala.scalajs.sbtplugin.env.nodejs.NodeJSEnv
import scala.scalajs.sbtplugin.ScalaJSPlugin._

import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys._

object Build extends sbt.Build{

  lazy val root = project.in(file(".")).aggregate()

  lazy val sharedSettings = Seq(
    scalaVersion := "2.11.2",
    organization := "uk.co.turingatemyhamster",
    version := "0.1.0",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "acyclic" % "0.1.2" % "provided",
      "com.lihaoyi" %%% "utest" % "0.2.3"
    ),
    scalacOptions := Seq("-explaintypes"),
    addCompilerPlugin("com.lihaoyi" %% "acyclic" % "0.1.2")
  ) ++ net.virtualvoid.sbt.graph.Plugin.graphSettings

  lazy val sharedBaseSettings = Seq(
    libraryDependencies ++= Seq(
      "uk.co.turingatemyhamster" %%% "datatree" % "0.1.0"
    ),
    unmanagedSourceDirectories in Compile +=
      (baseDirectory in root).value / "base" / "src"/ "main" / "scala",
    addCompilerPlugin("com.lihaoyi" %% "acyclic" % "0.1.2")
  )

  lazy val sharedPackagesSettings = Seq(
    unmanagedSourceDirectories in Compile +=
      (baseDirectory in root).value / "packages" / "src"/ "main" / "scala",
    addCompilerPlugin("com.lihaoyi" %% "acyclic" % "0.1.2")
  )

  lazy val baseJvm = project.
    in(file("baseJvm")).
    settings(sharedSettings:_*).
    settings(sharedBaseSettings:_*)

  lazy val baseJs = project.
    in(file("baseJs")).
    settings(scalaJSSettings:_*).
    settings(sharedSettings:_*).
    settings(sharedBaseSettings:_*)

  lazy val packagesJvm = project.
    in(file("packagesJvm")).
    dependsOn(baseJvm % "compile->compile;test->test").
    settings(sharedSettings:_*).
    settings(sharedPackagesSettings:_*).
    settings(scalacOptions ++= (Seq("-Ymacro-debug-verbose")))

  lazy val packagesJs = project.
    in(file("packagesJs")).
    dependsOn(baseJs % "compile->compile;test->test").
    settings(scalaJSSettings:_*).
    settings(sharedSettings:_*).
    settings(sharedPackagesSettings:_*)

  lazy val jvm = project.
    in(file("jvm")).
    dependsOn(packagesJvm % "compile->compile;test->test").
    settings(sharedSettings:_*)

  lazy val js = project.
    in(file("js")).
    dependsOn(packagesJs % "compile->compile;test->test").
    settings(scalaJSSettings:_*).
    settings(sharedSettings:_*)
}
