package better_paths.path_sugar

import org.apache.hadoop.fs.{FileSystem, Path}
import org.scalatest.enablers.Size

trait PathSize {
  implicit def pathSize(implicit fs: FileSystem): Size[Path] = new Size[Path] {
    override def sizeOf(obj: Path): Long = fs.getFileStatus(obj).getLen
  }
}
