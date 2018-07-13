package better_paths

import java.io.{BufferedReader, InputStreamReader}
import java.nio.charset.{Charset, StandardCharsets}
import java.util.stream.Collectors

import better_paths.Dsl.touch
import org.apache.commons.io.IOUtils
import org.apache.hadoop.fs.{FileSystem, Path}
import resource.managed
import scala.collection.JavaConverters._

object PathContent {
  trait Implicits {
    implicit class PathContentOps(
        path: Path
      )(implicit
        fs: FileSystem,
        charset: Charset = StandardCharsets.UTF_8) {
      def contentAsString: String =
        IOUtils.toString(fs.open(path), charset)

      def newBufferedReader: BufferedReader = {
        val decoder = charset.newDecoder
        val reader = new InputStreamReader(fs.open(path), decoder)
        new BufferedReader(reader)
      }

      def lines: Seq[String] =
        managed(newBufferedReader).acquireAndGet {
          _.lines().collect(Collectors.toList())
        }.asScala

      def lineIterator: Iterator[String] =
        newBufferedReader.lines().iterator().asScala

      def <(line: String): Path = {
        managed(fs.create(path, true)).acquireAndGet {
          _.write(line.getBytes(charset))
        }
        path
      }

      def <<(line: String): Path = {
        managed(fs.append(touch(path))).acquireAndGet {
          _.write(line.getBytes(charset))
        }
        path
      }

      // TODO: should we use copyMerge instead of concat?
      def <|(paths: Seq[Path]): Path = {
        fs.concat(touch(path), paths.toArray)
        path
      }

      def |>:(paths: Seq[Path]): Unit = <|(paths)
    }
  }

  object Implicits extends Implicits

}
