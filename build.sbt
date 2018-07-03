val hadoopVersion: String = "2.8.3"

lazy val miniClusterDependencies = Seq(
  "org.apache.hadoop" % "hadoop-hdfs" % hadoopVersion % "compile,test" classifier "" classifier "tests",
  "org.apache.hadoop" % "hadoop-common" % hadoopVersion % "compile,test" classifier "" classifier "tests",
  "org.apache.hadoop" % "hadoop-minicluster" % hadoopVersion % "compile,test"
)

lazy val common = project.in(file("common"))
  .settings(
    scalaVersion := "2.11.7",
    libraryDependencies ++= Seq(
      "org.apache.hadoop" % "hadoop-common" % hadoopVersion % "compile,test" classifier "" classifier "tests",
      "org.scalactic" %% "scalactic" % "3.0.5",
      "org.scalatest" %% "scalatest" % "3.0.5"
    )
  )

lazy val testSugar = project
  .in(file("scalatest_sugar"))
  .dependsOn(common)
  .settings(
    name := "better-paths-scalatest-sugar",
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
    name := "better-paths",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.11.7",
    libraryDependencies ++= Seq(
      "org.scalactic" %% "scalactic" % "3.0.5" % Test,
      "com.jsuereth" % "scala-arm_2.11" % "2.0",
      "org.scalatest" %% "scalatest" % "3.0.5" % Test
    ) ++ miniClusterDependencies
  )

