organization := "Battleship"
val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.4"

lazy val rootProject = (project in file(".")).settings(

  organization := "Battleship",
  name := "Battleship-SA",
  version := "1.1",
  scalaVersion := "2.13.5",

  /** language support dependencies */
  libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play-json" % "2.9.2",
  ),

  /** graphical and event dependencies */
  libraryDependencies ++= Seq(
    "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
  ),

  /** akka http dependencies */
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion
  ),

  /** test dependencies */
  libraryDependencies ++= Seq(
    // "org.scalatest" %% "scalatest" % "3.2.7" % "test",
    // "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion,
    // "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion,
    // "org.mockito" % "mockito-core" % "2.8.47" % "test",
    // "org.mockito" %% "mockito-scala" % "1.15.0" % "test"
  ),

)

lazy val model = project in file("Model")

lazy val controller = project in file("Controller")

lazy val gui = project in file("Gui")

lazy val tui = project in file("Tui")

lazy val db = project in file("DBDockerfiles")
