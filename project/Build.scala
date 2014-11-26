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
  lazy val baseSharedJvm      = baseModule.jvmShared().settings(baseSharedSettings : _*)
  lazy val baseSharedJs       = baseModule.jsShared(baseSharedJvm).settings(baseSharedSettings : _*)

  val packagesModule = XModule(id = "sbol2-packages", baseDir = "packages", defaultSettings = packagesSettings)

  lazy val packages           = packagesModule.project(packagesJvm, packagesJs).settings(packageBin in Compile := file(""))
  lazy val packagesJvm        = packagesModule.jvmProject(baseJvm).dependsOn(baseJvm)
  lazy val packagesJs         = packagesModule.jsProject(packagesSharedJs).dependsOn(baseJs)
  lazy val packagesSharedJvm  = packagesModule.jvmShared().dependsOn(baseSharedJvm)
  lazy val packagesSharedJs   = packagesModule.jsShared(packagesSharedJvm).dependsOn(baseSharedJs)

  lazy val generalSettings = Seq(
    scalaVersion := "2.11.4",
    crossScalaVersions := Seq("2.11.4", "2.11.2"),
    scalacOptions ++= Seq("-deprecation", "-unchecked"),
    organization := "uk.co.turingatemyhamster",
    version := "0.1.1")

  lazy val baseSettings = generalSettings
  lazy val packagesSettings = generalSettings

  lazy val baseSharedSettings = Seq(
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "uk.co.turingatemyhamster" %%%! "datatree" % "0.1.1"
    )
  )
}
