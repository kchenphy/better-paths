package better_paths

import better_paths.Dsl.{ln, mkdirs, touchz}
import better_paths.common.{TempPathProvider, TestMiniDFSCluster}
import better_paths.scalatest_sugar.PathSugar
import org.apache.hadoop.fs.Path
import org.scalatest.{FlatSpec, Matchers}

class PathNameSpec extends FlatSpec with Matchers with TestMiniDFSCluster with TempPathProvider with PathSugar {
  "basename" should "correctly return basename" in {
    val path = new Path("a") / "b"
    path.basename shouldBe "b"
  }

  "qualified" should "return a qualified path with `qualified`" in {
    val path = new Path("a") / "b"
    path.qualified shouldBe fs.makeQualified(path)
  }

  "resolved" should "return a resolved path with `resolved`" in {
    val path = tmpPath / "a" / "b"
    mkdirs(path)
    path.resolved shouldBe fs.resolvePath(path)
  }

  "isFile/isDirectory/isSymlink" should "correctly detect the type of path" in {
    touchz(tmpPath / "a" / "b")

    tmpPath / "a" should be a directory
    tmpPath / "a" shouldNot be a file
    tmpPath / "a" / "b" shouldNot be a directory
    tmpPath / "a" / "b" should be a file

    touchz(tmpPath / "c")
    ln(tmpPath / "d", tmpPath / "c")
    val status = (tmpPath / "d").status
    tmpPath / "d" should be a symlink
  }
}