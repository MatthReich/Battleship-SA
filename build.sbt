val scala3Version = "3.0.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "Battleship-SA",
    version := "0.1.0",
    scalaVersion := scala3Version,
    /**
     * graphical and event dependencies
     */
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-swing" % "3.0.0"),
    /**
     * test dependencies
     */
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.9" % "test"),
    /**
     * util dependencies
     */
    libraryDependencies ++= Seq(
      //"net.codingwell"   %% "scala-guice" % "5.0.1",
      "com.google.inject" % "guice"       % "5.0.1"))
