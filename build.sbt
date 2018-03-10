organization := "gaelrenoux"
name := "quixote-core"
version := "0.1-SNAPSHOT"
//maintainer := "Gaël Renoux"

scalaVersion := "2.11.11"
scalacOptions ++= Seq(
  "-feature", "-deprecation",
  "-language:postfixOps", "-language:reflectiveCalls", "-language:implicitConversions",
  "-Ywarn-dead-code", "-Ywarn-value-discard", "-Ywarn-unused"
)

/* Suppress problems with Scaladoc links */
scalacOptions in(Compile, doc) ++= Seq("-no-link-warnings")

val scalatestVersion = "3.0.1"
val typesafeConfigVersion = "1.3.1"
val slf4jVersion = "1.7.24"
val scalaLoggingVersion = "3.4.0" //3.5.0 does not support Scala 2.11 anymore
val shapelessVersion = "2.3.2"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % scalatestVersion % "test",
  "com.typesafe" % "config" % typesafeConfigVersion,
  "org.slf4j" % "slf4j-api" % slf4jVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
  "com.chuusai" %% "shapeless" % shapelessVersion
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "Typesafe" at "https://repo.typesafe.com/typesafe/maven-releases/",
  "Snowplow Repo" at "http://maven.snplow.com/releases/",
  /* Bintray is necessary : SBT itself needs a version of Scalaz missing from the standard public repositories. */
  "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases"
)
