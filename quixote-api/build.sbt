organization := "gaelrenoux"
name := "quixote-api"
version := "0.1-SNAPSHOT"

scalaVersion := "2.12.4"
crossScalaVersions := Seq("2.11.12", "2.12.4")
scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-language:postfixOps",
  "-language:reflectiveCalls",
  "-language:implicitConversions",
  "-Ywarn-dead-code",
  "-Ywarn-value-discard",
  "-Ywarn-unused"
)

/* Suppress problems with Scaladoc links */
//scalacOptions in(Compile, doc) ++= Seq("-no-link-warnings")

val scalatestVersion = "3.0.5"
val typesafeConfigVersion = "1.3.3"
val slf4jVersion = "1.7.25"
val scalaLoggingVersion = "3.8.0"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % scalatestVersion % "test",
  "com.typesafe" % "config" % typesafeConfigVersion,
  "org.slf4j" % "slf4j-api" % slf4jVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "Typesafe" at "https://repo.typesafe.com/typesafe/maven-releases/"
)


