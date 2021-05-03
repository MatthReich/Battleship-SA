organization := "Battleship"
name := "Battleship-SA-Model"

version := "0.1"
scalaVersion := "2.13.5"

val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.4"


/** language support dependencies */
libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.9.2",
  // "org.scala-lang.modules" %% "scala-xml" % "1.3.0",
)

/** util dependencies */
libraryDependencies ++= Seq(
  "net.codingwell" %% "scala-guice" % "4.2.11",
  "com.google.inject" % "guice" % "3.0",
)

/** graphical dependencies */
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
  "org.scalatest" %% "scalatest" % "3.2.7" % "test",
  "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion,
  "org.mockito" % "mockito-core" % "2.8.47" % "test",
  // "org.mockito" %% "mockito-scala" % "1.15.0" % "test"
)

/** database dependencies */
libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.3.3",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "mysql" % "mysql-connector-java" % "8.0.20",
  "org.mongodb.scala" %% "mongo-scala-driver" % "4.0.4",
)