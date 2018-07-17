package better_paths.pavement

import better_paths.{IsDirectory, IsFile}
import org.apache.hadoop.fs.{FileSystem, Path}
import org.scalatest.enablers.Emptiness

trait PathEmptiness {
  implicit def pathEmptiness(implicit fs: FileSystem): Emptiness[Path] =
    new Emptiness[Path] {

      override def isEmpty(thing: Path): Boolean = {
        thing match {
          case IsFile()      => fs.getFileStatus(thing).getLen == 0
          case IsDirectory() => fs.listStatus(thing).isEmpty
          case _             => isEmpty(fs.resolvePath(thing))
        }
      }
    }
}
