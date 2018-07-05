package better_paths.scalatest_sugar

import org.apache.hadoop.fs.{FileSystem, Path}
import org.scalatest.enablers.Existence

trait ExistInFileSystem {

  /** Enables syntax such as the following:
    *
    * <pre class="stHighlight">
    * implicit val existence = existIn(fs)
    *
    * val path = "/user/some_path"
    * path should exist
    * </pre>
    *
    * @param fs [[FileSystem]] of interest
    * @return an [[Existence]] instance to be used together with [[org.scalatest.words.MatcherWords#exist]].
    */
  implicit def existence(implicit fs: FileSystem): Existence[Path] =
    new Existence[Path] {
      override def exists(thing: Path): Boolean = fs.exists(thing)
    }
}
