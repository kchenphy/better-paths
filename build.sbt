val hadoopVersion: String = "2.8.3"

lazy val miniClusterDependencies = Seq(
  "org.apache.hadoop" % "hadoop-hdfs" % hadoopVersion % "compile,test" classifier "" classifier "tests",
  "org.apache.hadoop" % "hadoop-common" % hadoopVersion % "compile,test" classifier "" classifier "tests",
  "org.apache.hadoop" % "hadoop-minicluster" % hadoopVersion % "compile,test"
)

lazy val core = project
  .in(file("core"))
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
