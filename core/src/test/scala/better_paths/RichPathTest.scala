package better_paths

import better_paths.Dsl._
import better_paths.common.{TempPathProvider, TestMiniDFSCluster}
import better_paths.scalatest_sugar.PathSugar
import org.apache.hadoop.fs.Path
import org.scalatest._
import org.scalatest.prop.TableDrivenPropertyChecks

class RichPathTest extends FlatSpec with Matchers
  with TableDrivenPropertyChecks with TempPathProvider with TestMiniDFSCluster with PathSugar {

  "Path interpolation" should "work as expected" in {
    val user = "JohnDoe"
    val folder = "my_folder"
    val id = 123
    val path = p"/${user}/${folder}/${id}.txt"

    path shouldBe new Path("/JohnDoe/my_folder/123.txt")
  }

  "RichPath" should "build path with slash" in {
    val expected = new Path("a", "b")
    val testCases = Table(
      ("parent", "child"),
      ("a", "b"),
      ("a/", "b")
    )

    forAll(testCases) {
      (parent, child) => new Path(parent) / child shouldBe expected
    }

    new Path("a") / new Path("b") shouldBe expected
  }

  "basename" should "correctly return basename" in {
    val path = new Path("a") / "b"
    path.basename shouldBe "b"
  }

  "qualified" should "return a qualified path with `qualified`" in {
    val path = new Path("a") / "b"
    path.qualified shouldBe fs.makeQualified(path)
  }

  "resolved" should "return a resolved path with `resolved`" in {
    val path = tmpPath / "a" / "b"
    mkdirs(path)
    path.resolved shouldBe fs.resolvePath(path)
  }

  "exists" should "correctly return existence of path" in {
    val existing = tmpPath / "a" / "b"
    mkdirs(existing)

    val nonExisting = tmpPath / "a" / "c"
    existing should exist
    nonExisting shouldNot exist
  }

  "status" should "correctly return status of path" in {
    val path = tmpPath / "a"
    touch(path)
    path.status shouldBe fs.getFileStatus(path)
  }

  "length" should "correctly return length of path" in {
    val path = tmpPath / "a"
    path < "abc"
    path.length shouldBe "abc".length
  }

  "parent/children" should "return correct results" in {
    val parent = tmpPath / "a"
    val child1 = parent / "b"
    val child2 = parent / "c"
    mkdirs(child1)
    mkdirs(child2)

    (parent.children should contain theSameElementsAs Array(child1, child2)) (after being qualified)
    child1.parent shouldBe parent
    child2.parent shouldBe parent
  }

  "isFile/isDirectory/isSymlink" should "correctly detect the type of path" in {
    touchz(tmpPath / "a" / "b")

    tmpPath / "a" should be a directory
    tmpPath / "a" shouldNot be a file
    tmpPath / "a" / "b" shouldNot be a directory
    tmpPath / "a" / "b" should be a file

    touchz(tmpPath / "c")
    ln(tmpPath / "d", tmpPath / "c")
    val status = (tmpPath / "d").status
    tmpPath / "d" should be a symlink
  }

  "list/glob" should "collect correct results" in {
    touchz(tmpPath / "a/b/c")
    touchz(tmpPath / "a/d")
    mkdirs(tmpPath / "a/e")

    val testCases = Table(
      ("actual", "expected"),
      ((tmpPath / "a").listDirectories, List(tmpPath / "a/e", tmpPath / "a/b")),
      ((tmpPath / "a").listFiles, List(tmpPath / "a/d")),
      ((tmpPath / "a/*").globFiles, List(tmpPath / "a/d")),
      ((tmpPath / "a/*").globDirectories, List(tmpPath / "a/e", tmpPath / "a/b")),
      ((tmpPath / "a/*/c").globFiles, List(tmpPath / "a/b/c"))
    )

    forAll(testCases) {
      (actual, expected) => (actual should contain theSameElementsAs expected) (after being qualified)
    }
  }

}
