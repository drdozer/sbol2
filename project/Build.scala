import sbt._
import Keys._

import scala.scalajs.sbtplugin.env.nodejs.NodeJSEnv
import scala.scalajs.sbtplugin.ScalaJSPlugin._

import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys._

object Build extends sbt.Build{
  val cross = new utest.jsrunner.JsCrossBuild(
    organization := "uk.co.turingatemyhamster",

    version := "0.1.0",
    scalaVersion := "2.11.2",
    name := "sbol2",

    // Sonatype
    publishArtifact in Test := false,
    publishTo <<= version { (v: String) =>
      Some("releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
    },
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "acyclic" % "0.1.2" % "provided"
    ),
    autoCompilerPlugins := true,

    addCompilerPlugin("com.lihaoyi" %% "acyclic" % "0.1.2"),
    pomExtra :=
      <url>https://github.com/turingatemyhamster/sbol2</url>
      <developers>
        <developer>
          <id>turingatemyhamster</id>
          <name>Matthew Pocock</name>
          <url>https://github.com/turingatemyhamster</url>
        </developer>
      </developers>

  )

  val baseSettings = Seq(
    scalaVersion := "2.11.2",
    organization := "uk.co.turingatemyhamster",
    version := "0.1.0",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "acyclic" % "0.1.2" % "provided",
      "org.scala-lang" % "scala-reflect" % scalaVersion.value % "provided",
      "uk.co.turingatemyhamster" %%% "datatree" % "0.1.0",
      "com.lihaoyi" %%% "utest" % "0.2.3"
    ),
    addCompilerPlugin("com.lihaoyi" %% "acyclic" % "0.1.2")
  ) ++ net.virtualvoid.sbt.graph.Plugin.graphSettings

  lazy val baseJvm = project.in(file("base")).settings(baseSettings:_*).settings(
    target := target.value / "sbol2-base-jvm",
    moduleName := "sbol2-base"
  )

  lazy val baseJs = project.in(file("base")).settings(scalaJSSettings ++ baseSettings:_*).settings(
    target := target.value / "sbol2-base-js",
    moduleName := "sbol2-base"
  )

  lazy val packagesJvm = cross.jvm.
    dependsOn(baseJvm % "compile->compile;test->test").
    in(file("packages")).
    settings(baseSettings:_*).
    settings(
      //scalacOptions ++= Seq("-Ymacro-debug-verbose", "Ytyper-debug"),
      target := target.value / "sbol2-packages-jvm",
      moduleName := "sbol2-packages"
    )

  lazy val packagesJs = cross.js.
    dependsOn(baseJs % "compile->compile;test->test").
    in(file("packages")).
    settings(scalaJSSettings ++ baseSettings:_*).
    settings(
      //      scalacOptions ++= Seq("-Ymacro-debug-lite"),
      target := target.value / "sbol2-packages-js",
      moduleName := "sbol2-packages"
    )

  lazy val jvm = cross.jvm.
    dependsOn(packagesJvm % "compile->compile;test->test").
    in(file("jvm")).
    settings(baseSettings:_*).
    settings(
      moduleName := "sbol2"
    )

  lazy val js = cross.js.
    dependsOn(packagesJs % "compile->compile;test->test").
    in(file("js")).
    settings(scalaJSSettings ++ baseSettings:_*).
    settings(
      moduleName := "sbol2",
      (jsEnv in Test) := new NodeJSEnv
    )
}

