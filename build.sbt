import sbtbuildinfo.BuildInfoKeys

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(GitVersioning)
  .settings(
    name := "Undicator",
    organization := "org.github.ainr",
    version := "0.0.1",
    assembly / assemblyJarName := "App.jar",
    assembly / logLevel := Level.Info,
    buildInfoKeys ++= Seq[BuildInfoKey](
      name,
      version,
      scalaVersion,
      sbtVersion,
      resolvers,
      BuildInfoKey.action("buildTime") {
        System.currentTimeMillis
      },
      BuildInfoKey.action("gitHeadCommit") {
        git.gitHeadCommit.value map { sha => s"v$sha" }
      },
      BuildInfoKey.action("github") {
        "https://github.com/a-khakimov/undicator"
      }
    ),
    scalacOptions ++= Seq(
      "-language:postfixOps",
      "-language:implicitConversions",
      "-feature"
    ),
    buildInfoPackage := "org.github.ainr"
  )

libraryDependencies ++= {

  val telegramium = Seq(
    "io.github.apimorphism" %% "telegramium-core",
    "io.github.apimorphism" %% "telegramium-high"
  ).map(_ % "7.62.0")

  val catsEffect = Seq("org.typelevel" %% "cats-effect" % "3.3.14")
  val ciris = Seq("is.cir" %% "ciris" % "2.4.0")
  val cirisHocon = Seq("lt.dvim.ciris-hocon" %% "ciris-hocon" % "1.0.1")
  val sourcecode = Seq("com.lihaoyi" %% "sourcecode" % "0.3.0")
  val slf4j = Seq("org.slf4j" % "slf4j-api" % "1.7.36")
  val log4cats = Seq(
    "org.typelevel" %% "log4cats-core",
    "org.typelevel" %% "log4cats-slf4j"
  ).map(_ % "2.4.0")

  val circe = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-parser",
    "io.circe" %% "circe-generic"
  ).map(_ % "0.14.1")

  val scaffeine = Seq("com.github.blemale" %% "scaffeine" % "5.2.1")

  val nspl = Seq("io.github.pityka" %% "nspl-awt" % "0.6.0")

  val flyway = Seq("org.flywaydb" % "flyway-core" % "8.5.9")

  val doobie = Seq(
    "org.tpolecat" %% "doobie-postgres",
    "org.tpolecat" %% "doobie-hikari",
    "org.tpolecat" %% "doobie-refined"
  ).map(_ % "1.0.0-RC2")

  val refined = Seq(
    "eu.timepit" %% "refined",
    "eu.timepit" %% "refined-cats"
  ).map(_ % "0.10.1")

  Seq(
    telegramium,
    catsEffect,
    ciris,
    cirisHocon,
    slf4j,
    log4cats,
    sourcecode,
    circe,
    scaffeine,
    nspl,
    refined,
    flyway,
    doobie
  ).flatten
}

ThisBuild / assemblyMergeStrategy := {
  case PathList("org", "slf4j", xs @ _*) => MergeStrategy.first
  case x                                 => (ThisBuild / assemblyMergeStrategy).value(x)
}
