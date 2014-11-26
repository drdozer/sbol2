//// REQUIRED SETUP

// Setup bintray resolver - needed for many sbt plugins
resolvers += Resolver.url(
  "bintray-sbt-plugin-releases",
  url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(
  Resolver.ivyStylePatterns)

// Wrapper plugin for scalajs
addSbtPlugin("com.github.inthenow" % "sbt-scalajs" % "0.56.6")

//// END REQUIRED SETUP
