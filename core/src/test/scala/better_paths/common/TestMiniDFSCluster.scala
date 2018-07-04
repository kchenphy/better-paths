package better_paths.common

import java.nio.file.Files

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.hdfs.MiniDFSCluster
import org.scalatest.{BeforeAndAfterAll, Suite}


trait TestMiniDFSCluster extends BeforeAndAfterAll {
  self: Suite =>

  private var cluster: MiniDFSCluster = _

  implicit def fs: FileSystem = cluster.getFileSystem()

  override protected def beforeAll(): Unit = {
    super.beforeAll()

    val baseDir = Files.createTempDirectory("test_hdfs_" + System.nanoTime().toString).toFile.getAbsoluteFile
    val conf = new Configuration()
    conf.set(MiniDFSCluster.HDFS_MINIDFS_BASEDIR, baseDir.getAbsolutePath)
    cluster = new MiniDFSCluster.Builder(conf).build()
  }

  override protected def afterAll(): Unit = {
    cluster.shutdown(false, true)
    super.afterAll()
  }
}
