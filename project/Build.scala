import sbt._
import Keys._
import com.inthenow.sbt.scalajs._
import com.inthenow.sbt.scalajs.SbtScalajs._
import scala.scalajs.sbtplugin.ScalaJSPlugin._
import ScalaJSKeys._

object Build extends Build {

  val baseModule = XModule(id = "sbol2-base", baseDir = "base", defaultSettings = baseSettings)

  lazy val base               = baseModule.project(baseJvm, baseJs).settings(packageBin in Compile := file(""))
  lazy val baseJvm            = baseModule.jvmProject(baseSharedJvm)
  lazy val baseJs             = baseModule.jsProject(baseSharedJs)
  lazy val baseSharedJvm      = baseModule.jvmShared()
  lazy val baseSharedJs       = baseModule.jsShared()

  val packagesModule = XModule(id = "sbol2-packages", baseDir = "packages", defaultSettings = packagesSettings)

  lazy val packages           = packagesModule.project(packagesJvm, packagesJs).settings(packageBin in Compile := file(""))
  lazy val packagesJvm        = packagesModule.jvmProject(packagesSharedJvm, baseJvm)
  lazy val packagesJs         = packagesModule.jsProject(packagesSharedJs, baseJs)
  lazy val packagesSharedJvm  = packagesModule.jvmShared(baseSharedJvm)
  lazy val packagesSharedJs   = packagesModule.jsShared(baseSharedJs)

  lazy val generalSettings = Seq(
    scalaVersion := "2.11.4",
    crossScalaVersions := Seq("2.11.4", "2.11.2"),
    scalacOptions ++= Seq("-deprecation", "-unchecked"),
    organization := "uk.co.turingatemyhamster",
    version := "0.1.1")

  lazy val baseSettings = generalSettings
  lazy val packagesSettings = generalSettings

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
