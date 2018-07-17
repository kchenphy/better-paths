package better_paths

import better_paths.Dsl.{mkdirs, touchz}
import better_paths.scalatest_sugar.PathSugar
import better_paths.test_utils.{TempPathProvider, TestMiniDFSCluster}
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

class PathGlobSpec
    extends FlatSpec
    with Matchers
    with TestMiniDFSCluster
    with TempPathProvider
    with PathSugar
    with TableDrivenPropertyChecks {

  "list/glob" should "collect correct results" in {
    touchz(tmpPath / "b" / "c")
    touchz(tmpPath / "d")
    mkdirs(tmpPath / "e")

    val testCases = Table(
      ("actual", "expected"),
      (tmpPath.listDirectories, List(tmpPath / "e", tmpPath / "b")),
      (tmpPath.listFiles, List(tmpPath / "d")),
      ((tmpPath / "*").globFiles, List(tmpPath / "d")),
      ((tmpPath / "*").globDirectories, List(tmpPath / "e", tmpPath / "b")),
      ((tmpPath / "*" / "c").globFiles, List(tmpPath / "b" / "c"))
    )

    forAll(testCases) { (actual, expected) =>
      (actual should contain theSameElementsAs expected)(after being qualified)
    }
  }
}
