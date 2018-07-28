package better_paths

import better_paths.Dsl._
import better_paths.path_sugar.PathSugar
import better_paths.test_utils.{TempPathProvider, TestMiniDFSCluster}
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
    val nonChild1 = tmpPath / "d"
    val nonChild2 = "c"
    mkdirs(child1)
    mkdirs(child2)

    (tmpPath.children should contain theSameElementsAs Array(child1, child2))(
      after being qualified)
    child1.parent shouldBe tmpPath
    child2.parent shouldBe tmpPath

    tmpPath.contains(child1) shouldBe true
    tmpPath.contains(nonChild1) shouldBe false
    tmpPath should contain(child1)
    tmpPath should contain(child2)
    tmpPath shouldNot contain(nonChild1)
    tmpPath shouldNot contain(nonChild2)
  }

  "descendantsIterator" should "traverse descendants in pre-order" in {
    mkdirs(tmpPath / "b")
    touch(tmpPath / "b" / "c")
    touch(tmpPath / "b" / "d")
    touch(tmpPath / "e")
    touch(tmpPath / "f")

    val actual = tmpPath.descendantsIterator.toList
    val expected = List(".", "b", "b/c", "b/d", "e", "f").map(tmpPath / _)

    (actual should contain theSameElementsInOrderAs expected)(
      after being qualified)
  }
}
