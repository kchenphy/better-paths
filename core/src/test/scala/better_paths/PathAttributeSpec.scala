package better_paths

import Dsl._
import better_paths.test_utils.{TempPathProvider, TestMiniDFSCluster}
import path_sugar.PathSugar
import org.scalatest.{FlatSpec, Matchers}

class PathAttributeSpec
    extends FlatSpec
    with Matchers
    with TestMiniDFSCluster
    with TempPathProvider
    with PathSugar {
  "exists" should "correctly return existence of path" in {
    touch(tmpPath)
    tmpPath should exist

    val nonExisting = tmpPath / "a" / "c"
    nonExisting shouldNot exist
  }

  "status" should "correctly return status of path" in {
    touch(tmpPath)
    tmpPath.status shouldBe fs.getFileStatus(tmpPath)
  }

  "length" should "correctly return length of path" in {
    (tmpPath < "abc").length shouldBe "abc".length
  }

  "isFile/isDirectory/isSymlink" should "correctly detect the type of path" in {
    touchz(tmpPath / "a" / "b")

    tmpPath / "a" should be a directory
    // or equivalently
    tmpPath / "a" should matchPattern {
      case IsDirectory() => ()
    }
    tmpPath / "a" shouldNot be a file
    tmpPath / "a" / "b" shouldNot be a directory
    tmpPath / "a" / "b" should be a file

    touchz(tmpPath / "c")
    ln(tmpPath / "d", tmpPath / "c")
    tmpPath / "d" should be a symlink
  }
}
