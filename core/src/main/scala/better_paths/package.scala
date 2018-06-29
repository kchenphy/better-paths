import java.nio.charset.{Charset, StandardCharsets}

import org.apache.commons.io.IOUtils
import org.apache.hadoop.fs.{FileStatus, FileSystem, Path, PathFilter}
import resource._

package object better_paths {

  object RichPath {
    def IsFile(implicit fs: FileSystem): PathFilter = new PathFilter {
      override def accept(path: Path): Boolean = fs.isFile(path)
    }

    def IsDirectory(implicit fs: FileSystem): PathFilter = new PathFilter {
      override def accept(path: Path): Boolean = fs.isDirectory(path)
    }
  }


  implicit class RichPath(val path: Path) {

    import RichPath._

    def /(child: String): Path = new Path(path, child)

    def /(child: Path): Path = new Path(path, child)

    def basename: String = path.getName

    def qualified(implicit fs: FileSystem): Path = fs.makeQualified(path)

    def resolved(implicit fs: FileSystem): Path = fs.resolvePath(path)

    def status(implicit fs: FileSystem): FileStatus = fs.getFileStatus(path)

    def length(implicit fs: FileSystem): Long = fs.getFileStatus(path).getLen

    def exists(implicit fs: FileSystem): Boolean = fs.exists(path)

    def isFile(implicit fs: FileSystem): Boolean = fs.isFile(path)

    def isDirectory(implicit fs: FileSystem): Boolean = fs.isDirectory(path)

    def delete(recursive: Boolean = true)(implicit fs: FileSystem): Boolean = fs.delete(path, recursive)

    def touch(implicit fs: FileSystem): Path = if (!path.exists) touchz else path

    def touchz(implicit fs: FileSystem): Path = {
      fs.create(path, false, fs.getConf.getInt("io.file.buffer.size", 4096)).close()
      path
    }

    def mkdirs(implicit fs: FileSystem): Path = {
      fs.mkdirs(path)
      path
    }

    def globPath(pathFilter: PathFilter)(implicit fs: FileSystem): Array[Path] =
      fs.globStatus(path, pathFilter).map(_.getPath)

    def globDirectories(implicit fs: FileSystem): Array[Path] =
      globPath(IsDirectory)

    def globFiles(implicit fs: FileSystem): Array[Path] =
      globPath(IsFile)

    def listPath(pathFilter: PathFilter)(implicit fs: FileSystem): Array[Path] =
      fs.listStatus(path, pathFilter).map(_.getPath)

    def listDirectories(implicit fs: FileSystem): Array[Path] =
      listPath(IsDirectory)

    def listFiles(implicit fs: FileSystem): Array[Path] =
      listPath(IsFile)

    def <(line: String)(implicit fs: FileSystem, charset: Charset = StandardCharsets.UTF_8): Path = {
      managed(fs.create(path, true)).acquireAndGet { _.write(line.getBytes(charset)) }
      path
    }

    def <<(line: String)(implicit fs: FileSystem, charset: Charset = StandardCharsets.UTF_8): Path = {
      managed(fs.append(path.touch)).acquireAndGet { _.write(line.getBytes(charset)) }
      path
    }

    def `>:`(line: String)(implicit fs: FileSystem, charset: Charset = StandardCharsets.UTF_8): Unit =
      <(line)

    def <|(paths: Seq[Path])(implicit fs: FileSystem): Path = {
      fs.concat(path.touch, paths.toArray)
      path
    }

    def |>:(paths: Seq[Path])(implicit fs: FileSystem): Unit = <|(paths)

    def contentAsString(implicit fs: FileSystem, charset: Charset = StandardCharsets.UTF_8): String =
      IOUtils.toString(fs.open(path), charset)
  }

}
