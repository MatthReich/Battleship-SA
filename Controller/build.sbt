organization := "Battleship"
val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.4"
name := "Battleship-SA-Controller"

version := "1.1"

scalaVersion := "2.13.5"


libraryDependencies += "com.google.inject" % "guice" % "3.0"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.9.2"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.3.0"

libraryDependencies += "net.codingwell" %% "scala-guice" % "4.2.11"

libraryDependencies += "org.scalatest" %% "scalatest-wordspec" % "3.2.5" % "test"

libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion
)
