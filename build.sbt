import scala.languageFeature.experimental.macros

val hadoopVersion: String = "2.8.3"

lazy val miniClusterDependencies = Seq(
  "org.apache.hadoop" % "hadoop-hdfs" % hadoopVersion % "compile,test" classifier "" classifier "tests",
  "org.apache.hadoop" % "hadoop-common" % hadoopVersion % "compile,test" classifier "" classifier "tests",
  "org.apache.hadoop" % "hadoop-minicluster" % hadoopVersion % "compile,test"
)

lazy val macros = project
  .in(file("macros"))
  .settings(
    scalaVersion := "2.11.7",
    libraryDependencies ++= Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
  )

lazy val common = project
  .in(file("common"))
  .dependsOn(macros)
  .settings(
    scalaVersion := "2.11.7",
    libraryDependencies ++= Seq(
      "org.apache.hadoop" % "hadoop-common" % hadoopVersion % "compile,test" classifier "" classifier "tests",
      "org.scalactic" %% "scalactic" % "3.0.5",
      "org.scalatest" %% "scalatest" % "3.0.5"
    ),
    scalacOptions ++= Seq("-Xexperimental"),
    addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)
  )

lazy val testSugar = project
  .in(file("scalatest_sugar"))
  .dependsOn(common)
  .settings(
    name := "scalatest-sugar",
    organization := "com.github.kchenphy",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.11.7",
    libraryDependencies ++= Seq(
      "org.apache.hadoop" % "hadoop-common" % hadoopVersion % "compile,test" classifier "" classifier "tests",
      "org.scalactic" %% "scalactic" % "3.0.5",
      "org.scalatest" %% "scalatest" % "3.0.5"
    )
  )


lazy val core = project
  .in(file("core"))
  .dependsOn(testSugar, common)
  .settings(
    name := "better-paths-core",
    organization := "com.github.kchenphy",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.11.7",
    libraryDependencies ++= Seq(
      "org.scalactic" %% "scalactic" % "3.0.5" % Test,
      "com.jsuereth" % "scala-arm_2.11" % "2.0",
      "org.scalatest" %% "scalatest" % "3.0.5" % Test
    ) ++ miniClusterDependencies,
    addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)
  )

