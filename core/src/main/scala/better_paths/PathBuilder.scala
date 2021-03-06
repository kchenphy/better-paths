package better_paths

import org.apache.hadoop.fs.Path

object PathBuilder {

  trait Implicits {
    implicit class PathBuilderOps(path: Path) {
      def /(child: String): Path = new Path(path, child)

      def /(child: Path): Path = new Path(path, child)

      def /(child: Symbol): Path = /(child.name)

      def /[T](transformation: Function[Path, T]) = transformation.apply(path)
    }
  }

  object Implicits extends Implicits
}
