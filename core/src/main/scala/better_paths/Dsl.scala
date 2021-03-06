package better_paths

import org.apache.hadoop.fs.{FileSystem, Path}

object Dsl {

  @AddTry
  def delete(
      path: Path,
      recursive: Boolean = true
    )(implicit
      fs: FileSystem
    ): Path = {
    fs.delete(path, recursive)
    path
  }

  @AddTry
  def mkdirs(path: Path)(implicit fs: FileSystem): Path = {
    fs.mkdirs(path)
    path
  }

  @AddTry
  def touch(path: Path)(implicit fs: FileSystem): Path =
    if (!path.exists) touchz(path) else path

  @AddTry
  def touchz(path: Path)(implicit fs: FileSystem): Path = {
    fs.create(path, false, fs.getConf.getInt("io.file.buffer.size", 4096))
      .close()
    path
  }

  @AddTry
  def ln(link: Path, target: Path)(implicit fs: FileSystem): Unit =
    fs.createSymlink(target, link, true)

  def withWorkingDirectory[A](
      path: Path
    )(action: => A
    )(implicit fs: FileSystem
    ): A = {
    val pwd = fs.getWorkingDirectory
    fs.setWorkingDirectory(path)
    val result = action
    fs.setWorkingDirectory(pwd)
    result
  }

  val `..` : Path => Path = _.getParent

  val `.` : Path => Path = identity

  def `*`(implicit fs: FileSystem): Path => Array[Path] =
    path => fs.listStatus(path).map(_.getPath)

  def pwd(implicit fs: FileSystem): Path = fs.getWorkingDirectory

  def home(implicit fs: FileSystem): Path = fs.getHomeDirectory
}
