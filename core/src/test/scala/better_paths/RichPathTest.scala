package better_paths

import java.io.IOException
import java.nio.file.Files

import better_paths.scalatest_sugar.PathSugar
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.hdfs.MiniDFSCluster
import org.scalatest._
import org.scalatest.enablers.Existence
import org.scalatest.prop.TableDrivenPropertyChecks

trait TempPathProvider extends BeforeAndAfterEach {
  self: Suite =>
  var tmpPath: Path = _

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    tmpPath = new Path(System.getProperty("java.io.tmpdir"), System.nanoTime().toString)
  }
}

class RichPathTest extends FlatSpec with Matchers
  with TableDrivenPropertyChecks with TempPathProvider with PathSugar {

  val baseDir = Files.createTempDirectory("test_hdfs").toFile.getAbsoluteFile
  val conf = new Configuration()
  conf.set(MiniDFSCluster.HDFS_MINIDFS_BASEDIR, baseDir.getAbsolutePath)
  implicit val fs: FileSystem = new MiniDFSCluster.Builder(conf).build.getFileSystem

  implicit val pathExistence: Existence[Path] = existIn(fs)

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
    fs.mkdirs(path)
    path.resolved shouldBe fs.resolvePath(path)
  }

  "exists" should "correctly return existence of path" in {
    val existing = tmpPath / "a" / "b"
    fs.mkdirs(existing)

    val nonExisting = tmpPath / "a" / "c"
    existing should exist
    nonExisting shouldNot exist
  }

  "status" should "correctly return status of path" in {
    val path = tmpPath / "a"
    path.touch
    path.status shouldBe fs.getFileStatus(path)
  }

  "length" should "correctly return length of path" in {
    val path = tmpPath / "a"
    path < "abc"
    path.length shouldBe "abc".length
  }


  "touchz" should "create a zero-length file, or throw IOException if file already exists" in {
    val path = tmpPath / "a"
    path.touchz
    path should exist
    intercept[IOException] {
      path.touchz
    }
  }

  "touch" should "create a zero-length file only when file is non-existing, or do nothing if file already exists" in {
    (tmpPath / "a").touch
    tmpPath / "a" should exist

    (tmpPath / "a").touch
  }

  "mkdirs" should "create paths recursively" in {
    (tmpPath / "a" / "b").mkdirs
    tmpPath / "a" / "b" should be a directory
  }

  "delete parent" should "delete children recursively" in {
    val parent = tmpPath / "a"
    val path = parent / "b"
    path.touch
    path should exist

    parent.delete()
    parent shouldNot exist
    path shouldNot exist
  }

  "delete children" should "not delete parent" in {
    val parent = tmpPath / "a"
    val path = parent / "b"
    path.mkdirs
    path should exist

    path.delete()
    path shouldNot exist
    parent should exist
  }

  "isFile/isDirectory" should "correctly detect whether path is file/directory" in {
    (tmpPath / "a" / "b").touchz

    tmpPath / "a" should be a directory
    tmpPath / "a" shouldNot be a file
    tmpPath / "a" / "b" shouldNot be a directory
    tmpPath / "a" / "b" should be a file
  }

  "list/glob" should "collect correct results" in {
    (tmpPath / "a/b/c").touchz
    (tmpPath / "a/d").touchz
    (tmpPath / "a/e").mkdirs

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

  "<" should "correctly write content into file" in {
    val path = tmpPath / "a"
    path < "some content"
    path.contentAsString() shouldBe "some content"
  }

  "<<" should "correctly append content to file, even when path does not exist" in {
    val path = tmpPath / "a"
    path << "some content"
    path.contentAsString() shouldBe "some content"
  }

  "<|" should "correctly merge files" in {
    (tmpPath / "a") < "some content\n"
    (tmpPath / "b") < "some other content"
    tmpPath.listFiles |>: (tmpPath / "merged")
    (tmpPath / "merged").contentAsString() shouldBe "some content\nsome other content"
  }

}
