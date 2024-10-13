import Dependencies.*

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.4"

lazy val root = (project in file("."))
  .settings(
    name := "to-do-scala",
    libraryDependencies ++= Seq(
      Cats.core,
      Cats.effect,
      Http4s.dsl,
      Http4s.circe,
      Http4s.server,
      Circe.core,
      Circe.generic,
      Doobie.postgres,
      Doobie.postgresCirce,
      logback
    )
  )
