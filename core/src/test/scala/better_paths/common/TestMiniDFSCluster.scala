package better_paths.common

import java.nio.file.Files

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.hdfs.MiniDFSCluster
import org.scalatest.{BeforeAndAfterAll, Suite}


trait TestMiniDFSCluster {

  private lazy val fsImpl: FileSystem = {
    val baseDir = Files.createTempDirectory("test_hdfs").toFile.getAbsoluteFile
    val conf = new Configuration()
    conf.set(MiniDFSCluster.HDFS_MINIDFS_BASEDIR, baseDir.getAbsolutePath)
    new MiniDFSCluster.Builder(conf).build().getFileSystem
  }

  implicit def fs: FileSystem = fsImpl
}
