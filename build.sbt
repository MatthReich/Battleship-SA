organization := "Battleship"
val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.4"

lazy val rootProject = (project in file(".")).settings(

  organization := "Battleship",
  name := "Battleship-SA",
  version := "1.1",
  scalaVersion := "2.13.5",
  libraryDependencies += "com.google.inject" % "guice" % "3.0",

  libraryDependencies += "com.typesafe.play" %% "play-json" % "2.9.2",

  libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.3.0",

  libraryDependencies += "net.codingwell" %% "scala-guice" % "4.2.11",

  libraryDependencies += "org.scalatest" %% "scalatest-wordspec" % "3.2.5" % "test",

  libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0",

  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion
  ),

).dependsOn(model, controller, gui, tui)

lazy val model = project in file("Model")

lazy val controller = project in file("Controller")

lazy val gui = (project in file("Gui")).dependsOn(controller, model)

lazy val tui = (project in file("Tui"))
