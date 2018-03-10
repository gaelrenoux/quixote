import sbt.Keys.unmanagedClasspath

/* Common settings */
lazy val commonSettings = Seq(
  organization := "gaelrenoux",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.12.4",
  crossScalaVersions := Seq("2.11.12", "2.12.4"),
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-language:postfixOps",
    "-language:reflectiveCalls",
    "-language:implicitConversions",
    "-Ywarn-dead-code",
    "-Ywarn-value-discard",
    "-Ywarn-unused"
  ),

  /* Appending to unmanagedClasspath everything marked 'untransitive'  */
  unmanagedClasspath in Compile ++= update.value.select(configurationFilter("untransitive")),
  unmanagedClasspath in Test ++= update.value.select(configurationFilter("testUntransitive"))
)

/* Define compile-and-test-only dependencies. Provided is not enough, because I want the clients to fail at compile-time if they don't have the necessary libraries. */
lazy val Untransitive = config("untransitive").hide describedAs "Dependencies required when using this library, but not packaged."
lazy val TestUntransitive = config("testUntransitive") extend(Test, Untransitive)
ivyConfigurations ++= Seq(Untransitive, TestUntransitive)

lazy val V = new {
  val scalatest = "3.0.5"
  val typesafeConfig = "1.3.3"
  val slf4j = "1.7.25"
  val scalaLogging = "3.8.0"
  val akka = "2.5.11"
  val logback = "1.2.3"
}

lazy val commonDependencies = libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % V.scalatest % "test",
  "com.typesafe" % "config" % V.typesafeConfig,
  "org.slf4j" % "slf4j-api" % V.slf4j,
  "com.typesafe.scala-logging" %% "scala-logging" % V.scalaLogging,

  "ch.qos.logback" % "logback-classic" % V.logback % "test"
)

lazy val commonResolvers = resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "Typesafe" at "https://repo.typesafe.com/typesafe/maven-releases/"
)

lazy val root = (project in file("."))
  .aggregate(api, akka)
  .settings()

lazy val api = (project in file("quixote-api"))
  .settings(
    commonSettings,
    commonDependencies,
    commonResolvers
  )

lazy val akka = (project in file("quixote-akka"))
  .dependsOn(api)
  .settings(
    commonSettings,
    commonDependencies,
    commonResolvers,
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % V.akka % Untransitive,
      "com.typesafe.akka" %% "akka-testkit" % V.akka % TestUntransitive
    )
  )

