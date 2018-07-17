package better_paths

import better_paths.Dsl.mkdirs
import better_paths.common.{TempPathProvider, TestMiniDFSCluster}
import better_paths.scalatest_sugar.PathSugar
import org.scalatest.{FlatSpec, Matchers}

class PathStructureSpec
    extends FlatSpec
    with Matchers
    with TestMiniDFSCluster
    with TempPathProvider
    with PathSugar {
  "parent/children" should "return correct results" in {
    val child1 = tmpPath / "a"
    val child2 = tmpPath / "b"
    val nonChild = "c"
    mkdirs(child1)
    mkdirs(child2)

    (tmpPath.children should contain theSameElementsAs Array(child1, child2))(
      after being qualified)
    child1.parent shouldBe tmpPath
    child2.parent shouldBe tmpPath

    tmpPath.contains(child1) shouldBe true
    tmpPath should contain(child1)
    tmpPath should contain(child2)
    tmpPath shouldNot contain(nonChild)
  }
}
