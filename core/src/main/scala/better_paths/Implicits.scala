package better_paths

import java.io.{BufferedReader, InputStreamReader}
import java.nio.charset.{Charset, StandardCharsets}
import java.util.stream.Collectors

import better_paths.Dsl.touch
import org.apache.commons.io.IOUtils
import org.apache.hadoop.fs.{FileStatus, FileSystem, Path, PathFilter}
import resource.managed

import scala.collection.JavaConverters._

trait Implicits {

  /** Path interpolations, enabling syntax like:
    *
    * <pre>
    * val user = "john.doe"
    * val p = p"/${user}/file.txt"
    * </pre>
    *
    * @param sc underlying [[StringContext]] instance.
    */
  implicit class PathInterpolations(sc: StringContext) {
    def p(args: Any*): Path = new Path(sc.s(args: _*))
  }

  implicit class PathBuilderOps(path: Path) {
    def /(child: String): Path = new Path(path, child)

    def /(child: Path): Path = new Path(path, child)

    def /(child: Symbol): Path = new Path(path, child.name)
  }

  implicit class PathNameOps(path: Path) {
    def basename: String = path.getName

    def qualified(implicit fs: FileSystem): Path = fs.makeQualified(path)

    def resolved(implicit fs: FileSystem): Path = fs.resolvePath(path)
  }

  implicit class PathStructureOps(path: Path) {
    def parent: Path = path.getParent

    def children(implicit fs: FileSystem): Array[Path] =
      fs.listStatus(path).map(_.getPath)
  }

  implicit class PathAttributeOps(path: Path)(implicit fs: FileSystem) {
    def isFile: Boolean = fs.isFile(path)

    def isDirectory: Boolean = fs.isDirectory(path)

    def isSymlink: Boolean = fs.getFileLinkStatus(path).isSymlink

    def length: Long = fs.getFileStatus(path).getLen

    def status: FileStatus = fs.getFileStatus(path)

    def exists: Boolean = fs.exists(path)
  }

  implicit class PathGlobOps(path: Path)(implicit fs: FileSystem) {
    def globPath(pathFilter: PathFilter): Array[Path] =
      fs.globStatus(path, pathFilter).map(_.getPath)

    def globDirectories: Array[Path] = globPath(IsDirectory)

    def globFiles: Array[Path] = globPath(IsFile)

    def listPath(pathFilter: PathFilter): Array[Path] =
      fs.listStatus(path, pathFilter).map(_.getPath)

    def listDirectories: Array[Path] = listPath(IsDirectory)

    def listFiles: Array[Path] = listPath(IsFile)
  }

  implicit class PathContent(
      path: Path
    )(implicit
      fs: FileSystem,
      charset: Charset = StandardCharsets.UTF_8) {
    def contentAsString: String =
      IOUtils.toString(fs.open(path), charset)

    def newBufferedReader: BufferedReader = {
      val decoder = charset.newDecoder
      val reader = new InputStreamReader(fs.open(path), decoder)
      new BufferedReader(reader)
    }

    def lines: Seq[String] =
      managed(newBufferedReader).acquireAndGet {
        _.lines().collect(Collectors.toList())
      }.asScala

    def lineIterator: Iterator[String] =
      newBufferedReader.lines().iterator().asScala

    def <(line: String): Path = {
      managed(fs.create(path, true)).acquireAndGet {
        _.write(line.getBytes(charset))
      }
      path
    }

    def <<(line: String): Path = {
      managed(fs.append(touch(path))).acquireAndGet {
        _.write(line.getBytes(charset))
      }
      path
    }

    def `>:`(line: String): Unit = <(line)

    // TODO: should we use copyMerge instead of concat?
    def <|(paths: Seq[Path]): Path = {
      fs.concat(touch(path), paths.toArray)
      path
    }

    def |>:(paths: Seq[Path]): Unit = <|(paths)
  }
}
