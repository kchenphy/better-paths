package better_paths.path_sugar

import org.apache.hadoop.fs.{FileSystem, Path}
import org.scalatest.enablers.Length

trait PathLength {
  implicit def pathLength(implicit fs: FileSystem): Length[Path] =
    new Length[Path] {
      override def lengthOf(obj: Path): Long = fs.getFileStatus(obj).getLen
    }
}
