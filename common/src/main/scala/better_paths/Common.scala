package better_paths

import org.apache.hadoop.fs.{FileSystem, Path, PathFilter}
import org.scalatest.matchers.{BePropertyMatchResult, BePropertyMatcher}

abstract class PathPredicate(val name: String) {
  def apply(path: Path)(implicit fs: FileSystem): Boolean
}

object PathPredicate {
  implicit def predicateToPathFilter(predicate: PathPredicate)(implicit fs: FileSystem): PathFilter = new PathFilter {
    override def accept(path: Path): Boolean = predicate.apply(path)
  }

  implicit def predicateToBePropertyMatcher(predicate: PathPredicate)(implicit fs: FileSystem): BePropertyMatcher[Path] = new BePropertyMatcher[Path] {
    override def apply(path: Path): BePropertyMatchResult = new BePropertyMatchResult(predicate.apply(path), predicate.name)
  }
}

object IsFile extends PathPredicate("file") {
  override def apply(path: Path)(implicit fs: FileSystem): Boolean = fs.isFile(path)
}

object IsDirectory extends PathPredicate("directory") {
  override def apply(path: Path)(implicit fs: FileSystem): Boolean = fs.isDirectory(path)
}

object IsSymlink extends PathPredicate("symlink") {
  override def apply(path: Path)(implicit fs: FileSystem): Boolean = fs.getFileLinkStatus(path).isSymlink
}
