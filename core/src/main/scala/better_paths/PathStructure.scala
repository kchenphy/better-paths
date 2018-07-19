package better_paths

import org.apache.hadoop.fs.{FileSystem, Path}

import scala.collection.mutable

object PathStructure {

  private class PreOrderIterator(root: Path)(implicit fs: FileSystem)
      extends Iterator[Path] {
    private val stack: mutable.Stack[Path] = mutable.Stack(root)

    override def hasNext: Boolean = stack.nonEmpty

    override def next(): Path = {
      val h = stack.pop()
      h match {
        case IsDirectory() =>
          stack.pushAll(
            fs.listStatus(h)
              .map(_.getPath)
              .sorted(Ordering.by[Path, String](_.toString).reverse))
        case _ =>
      }
      h
    }
  }

  trait Implicits {

    implicit class PathStructureOps(path: Path) {
      def parent: Path = path.getParent

      def children(implicit fs: FileSystem): Array[Path] =
        fs.listStatus(path).map(_.getPath)

      def contains(child: Path)(implicit fs: FileSystem): Boolean =
        PathContains(path, child)

      def descendantsIterator(implicit fs: FileSystem): Iterator[Path] =
        new PreOrderIterator(path)
    }
  }

  object Implicits extends Implicits

}
