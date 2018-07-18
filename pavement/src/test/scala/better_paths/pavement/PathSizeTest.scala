package better_paths.pavement

import better_paths.test_utils.{TempPathProvider, TestMiniDFSCluster}
import org.apache.hadoop.fs.Path
import org.scalatest.{FlatSpec, Matchers}
import resource._

class PathSizeTest
    extends FlatSpec
    with Matchers
    with TestMiniDFSCluster
    with TempPathProvider
    with PathSize {

  "PathSize" should "return correct size" in {
    val ps = pathSize
    val path = new Path(tmpPath, "a")
    managed(fs.create(path)).acquireAndGet { dos =>
      {
        dos.writeBoolean(true)
        dos.write(1)
        dos.write("ab".getBytes)
      }
    }

    ps.sizeOf(path) shouldBe 4
  }
}
