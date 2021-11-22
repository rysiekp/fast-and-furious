name := """fast_and_furious"""
organization := "com.piktel.maciej"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.11"

libraryDependencies += guice
libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test,
  "org.mockito" %% "mockito-scala" % "1.9.0" % Test
)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "4.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "4.0.0",
  "com.h2database" % "h2" % "1.4.200",
  "xyz.nickr" % "jomdb" % "4.0.2",
  "io.swagger" %% "swagger-play2" % "1.7.1"
)