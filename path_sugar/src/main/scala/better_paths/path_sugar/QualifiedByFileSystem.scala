package better_paths.path_sugar

import org.apache.hadoop.fs.{FileSystem, Path}
import org.scalactic.Uniformity

trait QualifiedByFileSystem {

  /** Enables equality test of [[org.apache.hadoop.fs.Path]], based on qualified value. For example:
    *
    * <pre class="stHighlight">
    *   val localFs: LocalFileSystem = ???
    *   val path = new Path("/user/some_path")
    *   val samePath = new Path("file:///user/some_path")
    *
    *   path should equal samePath (after being qualifiedBy(localFs))
    * </pre>
    *
    * @param fs : [[org.apache.hadoop.fs.FileSystem]] of interest
    * @return a [[org.scalactic.Uniformity]] instance to be used together with equality test
    */
  def qualifiedBy(fs: FileSystem): Uniformity[Path] = new Uniformity[Path] {
    override def normalizedOrSame(b: Any): Any = b match {
      case p: Path => fs.makeQualified(p)
      case _       => b
    }
    override def normalizedCanHandle(b: Any): Boolean = b.isInstanceOf[Path]
    override def normalized(a: Path): Path = fs.makeQualified(a)
  }

  /** Enables equality test of [[Path]], based on qualified value. For example:
    *
    * <pre class="stHighlight">
    *   implicit val localFs: LocalFileSystem = ???
    *   val path = new Path("/user/some_path")
    *   val samePath = new Path("file:///user/some_path")
    *
    *   path should equal samePath (after being qualified)
    * </pre>
    *
    * @param fs: [[FileSystem]] of interest
    * @return a [[Uniformity]] instance to be used together with equality test
    */
  def qualified(implicit fs: FileSystem): Uniformity[Path] = qualifiedBy(fs)
}
