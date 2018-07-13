package better_paths

import org.apache.hadoop.fs.{FileSystem, Path, PathFilter}

object PathGlob {
  trait Implicits {
    implicit class PathGlobOps(path: Path)(implicit fs: FileSystem) {
      def globPath(pathFilter: PathFilter): Array[Path] =
        fs.globStatus(path, pathFilter).map(_.getPath)

      def globDirectories: Array[Path] =
        globPath(IsDirectory)

      def globFiles: Array[Path] = globPath(IsFile)

      def listPath(
          pathFilter: PathFilter
        )(implicit fs: FileSystem
        ): Array[Path] =
        fs.listStatus(path, pathFilter).map(_.getPath)

      def listDirectories: Array[Path] =
        listPath(IsDirectory)

      def listFiles: Array[Path] = listPath(IsFile)
    }
  }

  object Implicits extends Implicits

}
