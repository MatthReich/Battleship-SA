organization := "Battleship"
val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.4"
name := "Battleship-SA-Tui"

version := "1.1"

scalaVersion := "2.13.5"


/** language support dependencies */
libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.9.2",
)

/** util dependencies */
libraryDependencies ++= Seq(
  "net.codingwell" %% "scala-guice" % "4.2.11",
  "com.google.inject" % "guice" % "3.0",
)

/** graphical and event dependencies */
libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
)

/** akka http dependencies */
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
)

/** test dependencies */
libraryDependencies ++= Seq(
  // "org.scalatest" %% "scalatest" % "3.2.7" % "test",
  // "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion,
  // "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion,
  // "org.mockito" % "mockito-core" % "2.8.47" % "test",
  // "org.mockito" %% "mockito-scala" % "1.15.0" % "test"
)