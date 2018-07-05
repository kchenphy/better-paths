package better_paths.scalatest_sugar

import org.apache.hadoop.fs.{FileSystem, Path}
import org.scalatest.enablers.Emptiness

trait PathEmptiness {
  implicit def pathEmptiness(implicit fs: FileSystem): Emptiness[Path] =
    new Emptiness[Path] {
      override def isEmpty(thing: Path): Boolean = {
        assert(fs.exists(thing))
        val status = fs.getFileStatus(thing)
        if (status.isDirectory) fs.listStatus(thing).isEmpty
        else if (status.isFile) status.getLen == 0L
        else isEmpty(fs.resolvePath(thing))
      }
    }
}
