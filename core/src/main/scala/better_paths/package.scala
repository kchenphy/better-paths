import java.nio.charset.{Charset, StandardCharsets}

import org.apache.commons.io.IOUtils
import org.apache.hadoop.fs.{FileStatus, FileSystem, Path, PathFilter}
import resource._

package object better_paths extends Implicits {

  object RichPath {
    def IsFile(implicit fs: FileSystem): PathFilter = new PathFilter {
      override def accept(path: Path): Boolean = fs.isFile(path)
    }

    def IsDirectory(implicit fs: FileSystem): PathFilter = new PathFilter {
      override def accept(path: Path): Boolean = fs.isDirectory(path)
    }

  }


  implicit class RichPath(val path: Path)(implicit fs: FileSystem) {

    import RichPath._

    def /(child: String): Path = new Path(path, child)

    def /(child: Path): Path = new Path(path, child)

    def basename: String = path.getName

    def qualified: Path = fs.makeQualified(path)

    def resolved: Path = fs.resolvePath(path)

    def status: FileStatus = fs.getFileStatus(path)

    def length: Long = fs.getFileStatus(path).getLen

    def exists: Boolean = fs.exists(path)

    def isFile: Boolean = fs.isFile(path)

    def isDirectory: Boolean = fs.isDirectory(path)

    def delete(recursive: Boolean = true): Boolean = fs.delete(path, recursive)

    def touch: Path = if (!path.exists) touchz else path

    def touchz: Path = {
      fs.create(path, false, fs.getConf.getInt("io.file.buffer.size", 4096)).close()
      path
    }

    def mkdirs: Path = {
      fs.mkdirs(path)
      path
    }

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

    def <(line: String)(implicit charset: Charset = StandardCharsets.UTF_8): Path = {
      managed(fs.create(path, true)).acquireAndGet { _.write(line.getBytes(charset)) }
      path
    }

    def <<(line: String)(implicit charset: Charset = StandardCharsets.UTF_8): Path = {
      managed(fs.append(path.touch)).acquireAndGet { _.write(line.getBytes(charset)) }
      path
    }

    def `>:`(line: String)(implicit charset: Charset = StandardCharsets.UTF_8): Unit =
      <(line)

    def <|(paths: Seq[Path]): Path = {
      fs.concat(path.touch, paths.toArray)
      path
    }

    def |>:(paths: Seq[Path]): Unit = <|(paths)

    def contentAsString(implicit charset: Charset = StandardCharsets.UTF_8): String =
      IOUtils.toString(fs.open(path), charset)
  }


}
