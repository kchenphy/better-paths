package better_paths

import org.apache.hadoop.fs.{FileSystem, Path, PathFilter}
import org.scalatest.matchers.{BePropertyMatchResult, BePropertyMatcher}

abstract class PathPredicate(val name: String) {
  def apply(path: Path)(implicit fs: FileSystem): Boolean

  /** Enables pattern matching.
    */
  def unapply(path: Path)(implicit fs: FileSystem): Boolean = apply(path)
}

object PathPredicate {
  implicit def predicateToPathFilter(
      predicate: PathPredicate
    )(implicit
      fs: FileSystem
    ): PathFilter =
    (path: Path) => predicate.apply(path)

  implicit def predicateToBePropertyMatcher(
      predicate: PathPredicate
    )(implicit
      fs: FileSystem
    ): BePropertyMatcher[Path] =
    (path: Path) =>
      new BePropertyMatchResult(predicate.apply(path), predicate.name)
}

object IsFile extends PathPredicate("file") {
  override def apply(path: Path)(implicit fs: FileSystem): Boolean =
    fs.isFile(path)
}

object IsDirectory extends PathPredicate("directory") {
  override def apply(path: Path)(implicit fs: FileSystem): Boolean =
    fs.isDirectory(path)
}

object IsSymlink extends PathPredicate("symlink") {
  override def apply(path: Path)(implicit fs: FileSystem): Boolean =
    fs.getFileLinkStatus(path).isSymlink
}
