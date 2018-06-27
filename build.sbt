name := "better-paths"

version := "0.1"

scalaVersion := "2.11.7"

val hadoopVersion: String = "2.8.3"

libraryDependencies ++= Seq(
  "org.apache.hadoop" % "hadoop-common" % hadoopVersion,
  "org.scalactic" %% "scalactic" % "3.0.5" % Test,
  "com.jsuereth" % "scala-arm_2.11" % "2.0",
  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)

// Based on Hadoop Mini Cluster tests from Alpine's PluginSDK (Apache licensed)
// javax.servlet signing issues can be tricky, we can just exclude the dep
def excludeFromAll(items: Seq[ModuleID], group: String, artifact: String) =
  items.map(_.exclude(group, artifact))

def excludeJavaxServlet(items: Seq[ModuleID]) =
  excludeFromAll(items, "javax.servlet", "servlet-api")

lazy val miniClusterDependencies = Seq(
  "org.apache.hadoop" % "hadoop-hdfs" % hadoopVersion % "compile,test" classifier "" classifier "tests",
  "org.apache.hadoop" % "hadoop-common" % hadoopVersion% "compile,test" classifier "" classifier "tests",
  "org.apache.hadoop" % "hadoop-minicluster" % hadoopVersion % "compile,test"
)

libraryDependencies ++= miniClusterDependencies
