package better_paths

import org.apache.hadoop.fs.{FileSystem, Path}

object PathContains {
  def apply(parent: Path, child: Path)(implicit fs: FileSystem): Boolean = {
    val qualifiedChild = fs.makeQualified(child)
    fs.listStatus(parent).exists(_.getPath == qualifiedChild)
  }
}
