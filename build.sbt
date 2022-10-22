import sbtbuildinfo.BuildInfoKeys

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(GitVersioning)
  .settings(
    name := "TelegramBot-Template",
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
      }
    ),
    scalacOptions ++= Seq(
      "-language:postfixOps"
    )
  )

libraryDependencies ++= {

  val telegramium = Seq(
    "io.github.apimorphism" %% "telegramium-core",
    "io.github.apimorphism" %% "telegramium-high"
  ).map(_ % "7.62.0")

  val catsEffect = Seq("org.typelevel" %% "cats-effect").map(_ % "3.3.14")
  val ciris = Seq("is.cir" %% "ciris").map(_ % "2.4.0")
  val cirisHocon = Seq("lt.dvim.ciris-hocon" %% "ciris-hocon").map(_ % "1.0.1")
  val sourcecode = Seq("com.lihaoyi" %% "sourcecode" % "0.3.0")
  val slf4j = Seq("org.slf4j" % "slf4j-api" % "1.7.36")
  val log4cats = Seq(
    "org.typelevel" %% "log4cats-core", // Only if you want to Support Any Backend
    "org.typelevel" %% "log4cats-slf4j" // Direct Slf4j Support - Recommended
  ).map(_ % "2.4.0")

  Seq(
    telegramium,
    catsEffect,
    ciris,
    cirisHocon,
    slf4j,
    log4cats,
    sourcecode
  ).flatten
}

ThisBuild / assemblyMergeStrategy := {
  case PathList("org", "slf4j", xs @ _*) => MergeStrategy.first
  case x                                 => (ThisBuild / assemblyMergeStrategy).value(x)
}
