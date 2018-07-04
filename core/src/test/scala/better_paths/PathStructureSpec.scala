package better_paths

import better_paths.Dsl.mkdirs
import better_paths.common.{TempPathProvider, TestMiniDFSCluster}
import better_paths.scalatest_sugar.PathSugar
import org.scalatest.{FlatSpec, Matchers}

class PathStructureSpec extends FlatSpec with Matchers with TestMiniDFSCluster with TempPathProvider with PathSugar {
  "parent/children" should "return correct results" in {
    val parent = tmpPath / "a"
    val child1 = parent / "b"
    val child2 = parent / "c"
    mkdirs(child1)
    mkdirs(child2)

    (parent.children should contain theSameElementsAs Array(child1, child2)) (after being qualified)
    child1.parent shouldBe parent
    child2.parent shouldBe parent
  }

}
