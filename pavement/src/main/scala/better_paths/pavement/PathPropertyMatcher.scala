package better_paths.pavement

import better_paths.{IsDirectory, IsFile, IsSymlink}
import org.apache.hadoop.fs.{FileSystem, Path}
import org.scalatest.matchers.BePropertyMatcher

trait PathPropertyMatcher {

  def file(implicit fs: FileSystem): BePropertyMatcher[Path] = IsFile

  def directory(implicit fs: FileSystem): BePropertyMatcher[Path] = IsDirectory

  def symlink(implicit fs: FileSystem): BePropertyMatcher[Path] = IsSymlink
}
