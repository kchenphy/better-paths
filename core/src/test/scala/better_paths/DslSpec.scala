package better_paths

import java.io.IOException

import better_paths.Dsl._
import better_paths.path_sugar.PathSugar
import better_paths.test_utils.{TempPathProvider, TestMiniDFSCluster}
import org.apache.hadoop.fs.Path
import org.scalatest._

class DslSpec
    extends FlatSpec
    with Matchers
    with TempPathProvider
    with PathSugar
    with TestMiniDFSCluster
    with Inspectors {

  "touch" should "create a zero-length file only when file is non-existing, or do nothing if file already exists" in {
    touch(tmpPath / "a")
    tmpPath / "a" should exist
    tmpPath / "a" shouldBe empty
  }

  "touchz" should "create a zero-length file, or throw IOException if file already exists" in {
    val path = tmpPath / "a"
    touchz(path)
    path should exist
    path shouldBe empty

    intercept[IOException] {
      touchz(path)
    }
  }

  "mkdirs" should "create paths recursively" in {
    mkdirs(tmpPath / "a" / "b")
    (tmpPath / "a" / "b") should be a directory
    (tmpPath / "a" / "b") shouldBe empty
  }

  private def setupPaths(root: Path): Seq[Path] = {
    touch(root / "a")
    mkdirs(root / "b")
    touch(root / "c" / "d")

    Seq(root / "a", root / "b", root / "c", root / "c", root / "c" / "d")
  }

  "delete parent" should "delete children" in {
    val paths = setupPaths(tmpPath)
    forAll(paths) { _ should exist }

    delete(tmpPath, recursive = true)
    forAll(paths) { _ shouldNot exist }
  }

  "delete non-empty parent directory with recursive" should "cause exception" in {
    val paths = setupPaths(tmpPath)
    forAll(paths) { _ should exist }

    intercept[IOException] {
      delete(tmpPath, recursive = false)
    }
  }

  "withWorkingDirectory" should "cause all action in a given directory, and upon exit reset current directory" in {
    val oldDir = tmpPath / "old"
    val newDir = tmpPath / "new"

    fs.setWorkingDirectory(oldDir)

    withWorkingDirectory(newDir) {
      touch(p"someFile")
      mkdirs(p"someDirectory")
    }

    fs.getWorkingDirectory shouldBe oldDir

    newDir / "someFile" should be a file
    newDir / "someDirectory" should be a directory
    oldDir / "someFile" shouldNot exist
    oldDir / "someDirectory" shouldNot exist
  }

  "home" should "return hadoop home directory" in {
    home shouldBe fs.getHomeDirectory
  }

  "pwd" should "return current working directory" in {
    val p = tmpPath / "abc"
    withWorkingDirectory(p) {
      pwd shouldEqual p
    }
  }
}
