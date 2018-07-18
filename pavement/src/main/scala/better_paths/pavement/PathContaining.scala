package better_paths.pavement

import better_paths.PathContains
import org.apache.hadoop.fs.{FileSystem, Path}
import org.scalatest.enablers.Containing

trait PathContaining {

  implicit def pathContaining(implicit fs: FileSystem): Containing[Path] =
    new Containing[Path] {

      override def contains(container: Path, element: Any): Boolean =
        element match {
          case child: Path => PathContains(container, child)
          case _           => false
        }

      override def containsOneOf(container: Path, elements: Seq[Any]): Boolean =
        elements.exists(e => contains(container, e))

      override def containsNoneOf(
          container: Path,
          elements: Seq[Any]
        ): Boolean =
        !containsOneOf(container, elements)
    }
}
