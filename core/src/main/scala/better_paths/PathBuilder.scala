package better_paths

import org.apache.hadoop.fs.{FileSystem, Path}

object PathBuilder {

  trait Implicits {
    implicit class PathBuilderOps(path: Path) {
      def /(child: String): Path = new Path(path, child)

      def /(child: Path): Path = new Path(path, child)

      def /(child: Symbol): Path = /(child.name)

      def /[T](transformation: Function[Path, T]) = transformation.apply(path)
    }
  }

  val `..` : Path => Path = _.getParent

  val `.` : Path => Path = identity

  def `*`(implicit fs: FileSystem): Path => Array[Path] =
    path => fs.listStatus(path).map(_.getPath)

  def home(implicit fs: FileSystem): Path = fs.getHomeDirectory

  object Implicits extends Implicits
}
