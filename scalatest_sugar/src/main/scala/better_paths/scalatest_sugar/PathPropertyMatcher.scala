package better_paths.scalatest_sugar

import org.apache.hadoop.fs.{FileSystem, Path}
import org.scalatest.matchers.{BePropertyMatchResult, BePropertyMatcher}

trait PathPropertyMatcher {
  class IsFileMatcher(fs: FileSystem) extends BePropertyMatcher[Path] {
    def apply(left: Path) = BePropertyMatchResult(fs.isFile(left), "file")
  }

  class IsDirectoryMatcher(fs: FileSystem) extends BePropertyMatcher[Path] {
    def apply(left: Path) = BePropertyMatchResult(fs.isDirectory(left), "directory")
  }

  class IsSymlinkMatcher(fs: FileSystem) extends BePropertyMatcher[Path] {
    def apply(left: Path)= BePropertyMatchResult(fs.getFileLinkStatus(left).isSymlink, "symlink")
  }

  /** Enables matcher test like the following:
    *
    * <pre>
    *   my_path should be a 'file
    *   my_path2 shouldNot be a 'file
    * </pre>
    *
    * @param fs: [[FileSystem]] of interest
    * @return an instance of [[IsFileMatcher]]
    */
  def file(implicit fs: FileSystem) = new IsFileMatcher(fs)

  /** Enables matcher test like the following:
    *
    * <pre>
    *   my_path should be a 'directory
    *   my_path2 shouldNot be a 'directory
    * </pre>
    *
    * @param fs: [[FileSystem]] of interest
    * @return an instance of [[IsDirectoryMatcher]]
    */
  def directory(implicit fs: FileSystem) = new IsDirectoryMatcher(fs)

  def symlink(implicit fs: FileSystem) = new IsSymlinkMatcher(fs)
}
