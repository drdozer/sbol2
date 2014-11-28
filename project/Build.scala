import sbt._
import Keys._
import com.inthenow.sbt.scalajs._
import com.inthenow.sbt.scalajs.SbtScalajs._
import scala.scalajs.sbtplugin.ScalaJSPlugin._
import ScalaJSKeys._

object Build extends Build {

  val baseModule = XModule(id = "sbol2-base", baseDir = "base", defaultSettings = baseSettings)

  lazy val base                 = baseModule.project(basePlatformJvm, basePlatformJs)
  lazy val basePlatformJvm      = baseModule.jvmProject(baseSharedJvm).settings(basePlatformJvmSettings : _*)
  lazy val basePlatformJs       = baseModule.jsProject(baseSharedJs).settings(basePlatformJsSettings : _*)
  lazy val baseSharedJvm        = baseModule.jvmShared().settings(baseSharedSettings : _*)
  lazy val baseSharedJs         = baseModule.jsShared(baseSharedJvm).settings(baseSharedSettings : _*)

  val packagesModule = XModule(id = "sbol2-packages", baseDir = "packages", defaultSettings = packagesSettings)

  lazy val packages             = packagesModule.project(packagesPlatformJvm, packagesPlatformJs)
  lazy val packagesPlatformJvm  = packagesModule.jvmProject(packagesSharedJvm).dependsOn(basePlatformJvm)
  lazy val packagesPlatformJs   = packagesModule.jsProject(packagesSharedJs).dependsOn(basePlatformJs)
  lazy val packagesSharedJvm    = packagesModule.jvmShared().dependsOn(baseSharedJvm)
  lazy val packagesSharedJs     = packagesModule.jsShared(packagesSharedJvm).dependsOn(baseSharedJs)

  lazy val generalSettings = Seq(
    scalaVersion := "2.11.4",
    crossScalaVersions := Seq("2.11.4", "2.11.2"),
    scalacOptions ++= Seq("-deprecation", "-unchecked"),
    organization := "uk.co.turingatemyhamster",
    version := "0.1.1")

  lazy val baseSettings = generalSettings
  lazy val packagesSettings = generalSettings

  def baseSharedSettings = Seq(
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "uk.co.turingatemyhamster" %%%! "datatree_shared" % "0.1.2"
    )
  )

  def basePlatformJvmSettings = Seq(
    libraryDependencies ++= Seq(
      "uk.co.turingatemyhamster" %% "datatree" % "0.1.2"
    )
  )

  def basePlatformJsSettings = Seq(
    libraryDependencies ++= Seq(
      "uk.co.turingatemyhamster" %%% "datatree" % "0.1.2"
    )
  )
}
