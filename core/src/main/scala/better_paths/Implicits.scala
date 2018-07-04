package better_paths

import java.io.{BufferedReader, InputStream, InputStreamReader}
import java.nio.charset.{Charset, StandardCharsets}
import java.util.stream.Collectors

import org.apache.commons.io.IOUtils
import org.apache.hadoop.fs.{FileStatus, FileSystem, Path, PathFilter}

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
  }

  implicit class PathNameOps(path: Path) {
    def basename: String = path.getName

    def qualified(implicit fs: FileSystem): Path = fs.makeQualified(path)

    def resolved(implicit fs: FileSystem): Path = fs.resolvePath(path)
  }

  implicit class PathStructureOps(path: Path) {
    def parent: Path = path.getParent

    def children(implicit fs: FileSystem): Array[Path] = fs.listStatus(path).map(_.getPath)
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
    def globPath(pathFilter: PathFilter): Array[Path] = fs.globStatus(path, pathFilter).map(_.getPath)

    def globDirectories: Array[Path] = globPath(IsDirectory)

    def globFiles: Array[Path] = globPath(IsFile)

    def listPath(pathFilter: PathFilter): Array[Path] = fs.listStatus(path, pathFilter).map(_.getPath)

    def listDirectories: Array[Path] = listPath(IsDirectory)

    def listFiles: Array[Path] = listPath(IsFile)
  }

  implicit class PathContent(path: Path) {
    def contentAsString(implicit fs: FileSystem, charset: Charset = StandardCharsets.UTF_8): String =
      IOUtils.toString(fs.open(path), charset)

    def lines(implicit fs: FileSystem, charset: Charset = StandardCharsets.UTF_8): Seq[String] =
      newBufferedReader(fs.open(path), charset).lines().collect(Collectors.toList()).asScala

    def lineIterator(implicit fs: FileSystem, charset: Charset = StandardCharsets.UTF_8): Iterator[String] =
      newBufferedReader(fs.open(path), charset).lines().iterator().asScala

    private def newBufferedReader(inputStream: InputStream, charset: Charset): BufferedReader = {
      val decoder = charset.newDecoder
      val reader = new InputStreamReader(inputStream, decoder)
      new BufferedReader(reader)
    }
  }

}
