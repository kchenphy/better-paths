package better_paths

import org.apache.hadoop.fs.{FileSystem, Path}

object PathName {

  trait Implicits {

    implicit class PathNameOps(path: Path) {
      def basename: String = path.getName

      def qualified(implicit fs: FileSystem): Path = fs.makeQualified(path)

      def resolved(implicit fs: FileSystem): Path = fs.resolvePath(path)
    }

  }

  object Implicits extends Implicits

}
