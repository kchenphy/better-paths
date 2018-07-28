package better_paths.path_sugar

import better_paths.test_utils.{TempPathProvider, TestMiniDFSCluster}
import org.apache.hadoop.fs.Path
import org.scalatest.{FlatSpec, Matchers}
import resource.managed

class PathLengthTest
    extends FlatSpec
    with Matchers
    with TestMiniDFSCluster
    with TempPathProvider
    with PathLength {

  "PathSize" should "return correct size" in {
    val ps = pathLength
    val path = new Path(tmpPath, "a")
    managed(fs.create(path)).acquireAndGet { dos =>
      {
        dos.writeBoolean(true)
        dos.write(1)
        dos.write("ab".getBytes)
      }
    }

    ps.lengthOf(path) shouldBe 4
  }
}
