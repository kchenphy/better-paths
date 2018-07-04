package better_paths

import better_paths.common.{TempPathProvider, TestMiniDFSCluster}
import better_paths.scalatest_sugar.PathSugar
import org.scalatest.{FlatSpec, Matchers}
import Dsl._

class PathContentSpec extends FlatSpec with Matchers with TestMiniDFSCluster with TempPathProvider with PathSugar {

  "contentAsString/lines/lineIterator" should "return correct paths respectively" in {
    tmpPath / "a" << "first line"
    tmpPath / "a" << "\n"
    tmpPath / "a" << "second line"

    (tmpPath / "a").contentAsString shouldBe "first line\nsecond line"
    (tmpPath / "a").lines shouldBe Seq("first line", "second line")

    val iterator = (tmpPath / "a").lineIterator
    iterator.toSeq shouldBe Seq("first line", "second line")
  }
}
