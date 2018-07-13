package better_paths

import Dsl._
import PathBuilder._
import common.{TempPathProvider, TestMiniDFSCluster}
import scalatest_sugar.PathSugar
import org.apache.hadoop.fs.Path
import org.scalatest.{FlatSpec, Matchers}

class PathBuilderSpec
    extends FlatSpec
    with Matchers
    with TestMiniDFSCluster
    with TempPathProvider
    with PathSugar {

  "/" can "be used with strings" in {
    new Path("a") / "b" shouldBe new Path("a/b")
  }

  it should "also work with symbols" in {
    new Path("a") / 'b shouldBe new Path("a/b")
  }

  it should "ignore parent's trailing slash" in {
    new Path("a/") / "b" shouldBe new Path("a/b")
  }

  it should "work with . and .." in {
    val p = new Path("a/b")
    p / `.` shouldBe new Path("a/b")
    p / `..` shouldBe new Path("a")
  }

  it should "list everything with *" in {
    touchz(tmpPath / "a")
    mkdirs(tmpPath / "b")

    (tmpPath / `*` should contain theSameElementsAs List(tmpPath / "a",
                                                         tmpPath / "b"))(
      after being qualified)
  }

  "home" should "return hadoop home directory" in {
    (home / 'a shouldEqual new Path("a"))(after being qualified)
  }
}
