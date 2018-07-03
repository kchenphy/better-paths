package better_paths

import java.nio.charset.{Charset, StandardCharsets}

import org.apache.hadoop.fs.{FileSystem, Path}
import resource.managed

object Dsl {

  implicit class IOOperations(path: Path)(implicit fs: FileSystem) {
    def <(line: String)(implicit charset: Charset = StandardCharsets.UTF_8): Path = {
      managed(fs.create(path, true)).acquireAndGet { _.write(line.getBytes(charset)) }
      path
    }

    def <<(line: String)(implicit charset: Charset = StandardCharsets.UTF_8): Path = {
      managed(fs.append(touch(path))).acquireAndGet { _.write(line.getBytes(charset)) }
      path
    }

    def `>:`(line: String)(implicit charset: Charset = StandardCharsets.UTF_8): Unit =
      <(line)

    // TODO: should we use copyMerge instead of concat?
    def <|(paths: Seq[Path]): Path = {
      fs.concat(touch(path), paths.toArray)
      path
    }

    def |>:(paths: Seq[Path]): Unit = <|(paths)
  }

  def delete(path: Path, recursive: Boolean = true)(implicit fs: FileSystem): Path = {
    fs.delete(path, recursive)
    path
  }

  def mkdirs(path: Path)(implicit fs: FileSystem): Path = {
    fs.mkdirs(path)
    path
  }

  def touch(path: Path)(implicit fs: FileSystem): Path = if (!path.exists) touchz(path) else path

  def touchz(path:Path)(implicit fs: FileSystem): Path = {
    fs.create(path, false, fs.getConf.getInt("io.file.buffer.size", 4096)).close()
    path
  }

  def ln(link: Path, target: Path)(implicit fs: FileSystem): Unit = fs.createSymlink(target, link, true)
}
