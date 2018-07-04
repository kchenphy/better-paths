package better_paths

import better_paths.Dsl.{mkdirs, touchz}
import better_paths.common.{TempPathProvider, TestMiniDFSCluster}
import better_paths.scalatest_sugar.PathSugar
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

class PathGlobSpec extends FlatSpec with Matchers with TestMiniDFSCluster with TempPathProvider with PathSugar
  with TableDrivenPropertyChecks {

  "list/glob" should "collect correct results" in {
    touchz(tmpPath / "a/b/c")
    touchz(tmpPath / "a/d")
    mkdirs(tmpPath / "a/e")

    val testCases = Table(
      ("actual", "expected"),
      ((tmpPath / "a").listDirectories, List(tmpPath / "a/e", tmpPath / "a/b")),
      ((tmpPath / "a").listFiles, List(tmpPath / "a/d")),
      ((tmpPath / "a/*").globFiles, List(tmpPath / "a/d")),
      ((tmpPath / "a/*").globDirectories, List(tmpPath / "a/e", tmpPath / "a/b")),
      ((tmpPath / "a/*/c").globFiles, List(tmpPath / "a/b/c"))
    )

    forAll(testCases) {
      (actual, expected) => (actual should contain theSameElementsAs expected) (after being qualified)
    }
  }
}
