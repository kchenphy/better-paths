val hadoopVersion: String = "2.8.3"
val scalatestVersion: String = "3.0.5"

lazy val formatAll   = taskKey[Unit]("Format all the source code which includes src, test, and build files")
lazy val checkFormat = taskKey[Unit]("Check all the source code which includes src, test, and build files")

lazy val commonSettings = Seq(
  organization := "com.github.kchenphy",
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.11.7",
  libraryDependencies += "org.apache.hadoop" % "hadoop-common" % hadoopVersion % Provided classifier "" classifier "tests",
  addCompilerPlugin(
    "org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full
  ),

  compile in Compile := (compile in Compile).dependsOn(formatAll).value,
  test in Test := (test in Test).dependsOn(checkFormat).value,

  formatAll := {
    (scalafmt in Compile).value
    (scalafmt in Test).value
    (scalafmtSbt in Compile).value
  },
  checkFormat := {
    (scalafmtCheck in Compile).value
    (scalafmtCheck in Test).value
    (scalafmtSbtCheck in Compile).value
  }
)

def scalatestDeps(
    scalatestVersion: String = scalatestVersion
)(conf: Configuration = Test) =
  Seq(
    "org.scalactic" %% "scalactic" % scalatestVersion,
    "org.scalatest" %% "scalatest" % scalatestVersion
  ).map(_ % conf)

def miniClusterDependencies(hadoopVersion: String = hadoopVersion) = Seq(
  "org.apache.hadoop" % "hadoop-hdfs" % hadoopVersion % Test classifier "" classifier "tests",
  "org.apache.hadoop" % "hadoop-minicluster" % hadoopVersion % Test
)

lazy val macros = project
  .in(file("macros"))
  .settings(
    commonSettings,
    scalaVersion := "2.11.7",
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value
    ),
    publishArtifact := false
  )

lazy val common = project
  .in(file("common"))
  .dependsOn(macros)
  .settings(
    commonSettings,
    publishArtifact := false,
    libraryDependencies ++= scalatestDeps(scalatestVersion)(Compile),
    scalacOptions ++= Seq("-Xexperimental")
  )

lazy val testSugar = project
  .in(file("scalatest_sugar"))
  .dependsOn(common)
  .settings(
    commonSettings,
    name := "better-paths-scalatest-sugar",
    libraryDependencies ++= scalatestDeps(scalatestVersion)(Compile)
  )

lazy val core = project
  .in(file("core"))
  .dependsOn(testSugar, common)
  .settings(
    commonSettings,
    name := "better-paths-core",
    libraryDependencies ++= scalatestDeps()()
      ++ Seq("com.jsuereth" %% "scala-arm" % "2.0")
      ++ miniClusterDependencies()
  )
