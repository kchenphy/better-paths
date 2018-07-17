package better_paths.scalatest_sugar

import better_paths.pavement.PathContaining
import better_paths.test_utils.{TempPathProvider, TestMiniDFSCluster}
import org.apache.hadoop.fs.Path
import org.scalatest.enablers.Containing
import org.scalatest.{FlatSpec, Matchers}

class PathContainingTest
    extends FlatSpec
    with Matchers
    with TestMiniDFSCluster
    with TempPathProvider
    with PathContaining {
  "pathContaining" should "return correct result" in {
    val pc: Containing[Path] = pathContaining

    val child1 = new Path(tmpPath, "a")
    val child2 = new Path(tmpPath, "b")
    fs.create(child1)
    fs.create(child2)

    val nonChild = new Path(tmpPath, "c")

    pc.contains(tmpPath, child1) shouldBe true
    pc.contains(tmpPath, child2) shouldBe true
    pc.contains(tmpPath, fs.makeQualified(child1)) shouldBe true
    pc.contains(tmpPath, "a") shouldBe false
    pc.contains(tmpPath, nonChild) shouldBe false

    pc.containsOneOf(tmpPath, Seq(child1)) shouldBe true
    pc.containsOneOf(tmpPath, Seq(child2)) shouldBe true

    pc.containsNoneOf(tmpPath, Seq(child2)) shouldBe false
  }
}
