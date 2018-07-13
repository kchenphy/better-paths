package better_paths

import org.apache.hadoop.fs.{FileSystem, Path}

object PathStructure {

  trait Implicits {
    implicit class PathStructureOps(path: Path) {
      def parent: Path = path.getParent

      def children(implicit fs: FileSystem): Array[Path] =
        fs.listStatus(path).map(_.getPath)
    }
  }

  object Implicits extends Implicits

}
