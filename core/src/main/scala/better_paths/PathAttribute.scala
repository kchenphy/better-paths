package better_paths

import org.apache.hadoop.fs.{FileStatus, FileSystem, Path}

object PathAttribute {

  trait Implicits {

    implicit class PathAttributeOps(path: Path)(implicit fs: FileSystem) {

      def isFile: Boolean = fs.isFile(path)

      def isDirectory: Boolean = fs.isDirectory(path)

      def isSymlink: Boolean =
        fs.getFileLinkStatus(path).isSymlink

      def length: Long = fs.getFileStatus(path).getLen

      def status: FileStatus = fs.getFileStatus(path)

      def exists: Boolean = fs.exists(path)
    }
  }

  object Implicits extends Implicits
}
