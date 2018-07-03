import java.nio.charset.{Charset, StandardCharsets}

import org.apache.commons.io.IOUtils
import org.apache.hadoop.fs.{FileStatus, FileSystem, Path, PathFilter}

package object better_paths extends Implicits {

  implicit class RichPath(val path: Path)(implicit fs: FileSystem) {

    def /(child: String): Path = new Path(path, child)

    def /(child: Path): Path = new Path(path, child)

    def basename: String = path.getName

    def qualified: Path = fs.makeQualified(path)

    def resolved: Path = fs.resolvePath(path)

    def status: FileStatus = fs.getFileStatus(path)
    def linkStatus: FileStatus = fs.getFileLinkStatus(path)

    def length: Long = fs.getFileStatus(path).getLen

    def exists: Boolean = fs.exists(path)

    def isFile: Boolean = fs.isFile(path)

    def isDirectory: Boolean = fs.isDirectory(path)

    def isSymlink: Boolean = linkStatus.isSymlink

    def globPath(pathFilter: PathFilter): Array[Path] =
      fs.globStatus(path, pathFilter).map(_.getPath)

    def globDirectories: Array[Path] =
      globPath(IsDirectory)

    def globFiles: Array[Path] =
      globPath(IsFile)

    def listPath(pathFilter: PathFilter): Array[Path] =
      fs.listStatus(path, pathFilter).map(_.getPath)

    def listDirectories: Array[Path] =
      listPath(IsDirectory)

    def listFiles: Array[Path] =
      listPath(IsFile)

    def parent: Path = path.getParent
    def children: Array[Path] = fs.listStatus(path).map(_.getPath)

    def contentAsString(implicit charset: Charset = StandardCharsets.UTF_8): String =
      IOUtils.toString(fs.open(path), charset)
  }
}
