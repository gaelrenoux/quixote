organization := "gaelrenoux"
name := "quixote-akka"
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

/* Define compile-and-test-only dependencies. Provided is not enough, because I want the clients to fail at compile-time if they don't have the necessary libraries. */
lazy val Untransitive = config("untransitive").hide describedAs "Dependencies required when using this library, but not packaged."
lazy val TestUntransitive = config("testUntransitive") extend(Test, Untransitive)
ivyConfigurations ++= Seq(Untransitive, TestUntransitive)

val scalatestVersion = "3.0.5"
val typesafeConfigVersion = "1.3.3"
val slf4jVersion = "1.7.25"
val scalaLoggingVersion = "3.8.0"

val akkaVersion = "2.5.8"

libraryDependencies ++= Seq(

  "org.scalatest" %% "scalatest" % scalatestVersion % "test",
  "com.typesafe" % "config" % typesafeConfigVersion,
  "org.slf4j" % "slf4j-api" % slf4jVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,

  "com.typesafe.akka" %% "akka-actor" % akkaVersion % Untransitive,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % TestUntransitive
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "Typesafe" at "https://repo.typesafe.com/typesafe/maven-releases/"
)

/* Appending to unmanagedClasspath everything marked 'untransitive'  */
unmanagedClasspath in Compile ++= update.value.select(configurationFilter("untransitive"))
unmanagedClasspath in Test ++= update.value.select(configurationFilter("testUntransitive"))



