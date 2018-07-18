package better_paths

import better_paths.test_utils.{TempPathProvider, TestMiniDFSCluster}
import org.apache.hadoop.fs.Path
import org.scalatest.{FlatSpec, Matchers}
import resource._

class PathContainsTest
    extends FlatSpec
    with Matchers
    with TestMiniDFSCluster
    with TempPathProvider {
  "PathContains" should "return correct result" in {

    val child1 = new Path(tmpPath, "a")
    val child2 = new Path(tmpPath, "b")
    val nonChild = new Path(tmpPath, "d")
    managed(fs.create(child1)).acquireAndGet(_.write(0))
    managed(fs.create(child2)).acquireAndGet(_.write(0))

    PathContains(tmpPath, child1) shouldBe true
    PathContains(tmpPath, child2) shouldBe true
    PathContains(tmpPath, nonChild) shouldBe false

  }
}
