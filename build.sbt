organization := "Battleship"

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
).dependsOn(model, controller, gui, tui)

lazy val model = (project in file("Model")).settings(
  organization := "Battleship",
  name := "Battleship-SA-Model",
  version := "1.1",
  scalaVersion := "2.13.5",
  libraryDependencies += "com.google.inject" % "guice" % "3.0",

  libraryDependencies += "com.typesafe.play" %% "play-json" % "2.9.2",

  libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.3.0",

  libraryDependencies += "net.codingwell" %% "scala-guice" % "4.2.11",

  libraryDependencies += "org.scalatest" %% "scalatest-wordspec" % "3.2.5" % "test",

  libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
)

lazy val controller = (project in file("Controller")).settings(
  organization := "Battleship",
  name := "Battleship-SA-Controller",
  version := "1.1",
  scalaVersion := "2.13.5",
  libraryDependencies += "com.google.inject" % "guice" % "3.0",

  libraryDependencies += "com.typesafe.play" %% "play-json" % "2.9.2",

  libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.3.0",

  libraryDependencies += "net.codingwell" %% "scala-guice" % "4.2.11",

  libraryDependencies += "org.scalatest" %% "scalatest-wordspec" % "3.2.5" % "test",

  libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
).dependsOn(model)

lazy val gui = (project in file("Gui")).settings(
  organization := "Battleship",
  name := "Battleship-SA-Gui",
  version := "1.1",
  scalaVersion := "2.13.5",
  libraryDependencies += "com.google.inject" % "guice" % "3.0",

  libraryDependencies += "com.typesafe.play" %% "play-json" % "2.9.2",

  libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.3.0",

  libraryDependencies += "net.codingwell" %% "scala-guice" % "4.2.11",

  libraryDependencies += "org.scalatest" %% "scalatest-wordspec" % "3.2.5" % "test",

  libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
).dependsOn(controller, model)

lazy val tui = (project in file("Tui")).settings(
  organization := "Battleship",
  name := "Battleship-SA-Tui",
  version := "1.1",
  scalaVersion := "2.13.5",
  libraryDependencies += "com.google.inject" % "guice" % "3.0",

  libraryDependencies += "com.typesafe.play" %% "play-json" % "2.9.2",

  libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.3.0",

  libraryDependencies += "net.codingwell" %% "scala-guice" % "4.2.11",

  libraryDependencies += "org.scalatest" %% "scalatest-wordspec" % "3.2.5" % "test",

  libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
).dependsOn(controller, model)
