import sbt._
import sbt.Keys._
import com.inthenow.sbt.scalajs._
import com.inthenow.sbt.scalajs.SbtScalajs._
import scala.scalajs.sbtplugin.ScalaJSPlugin._
import ScalaJSKeys._
import bintray.Plugin._
import bintray.Keys._
import org.eclipse.jgit.lib._

object Build extends Build {
  val logger = ConsoleLogger()
  val baseVersion = "0.1.2"


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

  lazy val generalSettings = bintrayPublishSettings ++ Seq(
    scalaVersion := "2.11.4",
    crossScalaVersions := Seq("2.11.4", "2.10.4"),
    scalacOptions ++= Seq("-deprecation", "-unchecked"),
    organization := "uk.co.turingatemyhamster",
    version := makeVersion(baseVersion),
    resolvers += "drdozer Bintray Repo" at "http://dl.bintray.com/content/drdozer/maven",
    publishMavenStyle := true,
    repository in bintray := "maven",
    bintrayOrganization in bintray := None,
    logLevel in bintray := Level.Debug,
    licenses +=("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html"))
    )

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

  def fetchGitBranch(): String = {
    val builder = new RepositoryBuilder()
    builder.setGitDir(file(".git"))
    val repo = builder.readEnvironment().findGitDir().build()
    val gitBranch = repo.getBranch
    logger.info(s"Git branch reported as: $gitBranch")
    repo.close()
    val travisBranch = Option(System.getenv("TRAVIS_BRANCH"))
    logger.info(s"Travis branch reported as: $travisBranch")

    val branch = (travisBranch getOrElse gitBranch) replaceAll ("/", "_")
    logger.info(s"Computed branch is $branch")
    branch
  }

  def makeVersion(baseVersion: String): String = {
    val branch = fetchGitBranch()
    if(branch == "main") {
      baseVersion
    } else {
      val tjn = Option(System.getenv("TRAVIS_JOB_NUMBER"))
      s"$branch-$baseVersion${
        tjn.map("." + _) getOrElse ""
      }"
    }
  }
}
