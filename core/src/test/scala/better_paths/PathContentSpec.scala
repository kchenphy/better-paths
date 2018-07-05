package better_paths

import java.io.IOException

import better_paths.Dsl._
import better_paths.common.{TempPathProvider, TestMiniDFSCluster}
import better_paths.scalatest_sugar.PathSugar
import org.scalatest.{FlatSpec, Matchers}

class PathContentSpec extends FlatSpec with Matchers with TestMiniDFSCluster with TempPathProvider with PathSugar {

  "contentAsString/lines/lineIterator" should "return correct paths respectively" in {
    tmpPath << "first line" << "\n" << "second line"
    tmpPath.contentAsString shouldBe "first line\nsecond line"
    tmpPath.lines shouldBe Seq("first line", "second line")

    val iterator = tmpPath.lineIterator
    iterator.toSeq shouldBe Seq("first line", "second line")
  }

  "<" should "write string into a file" in {
    tmpPath < "Lorem Ipsum"

    tmpPath should exist
    tmpPath.contentAsString shouldBe "Lorem Ipsum"
  }

  it should "overwrite string into a file" in {
    tmpPath < "Lorem Ipsum" < "A different Lorem Ipsum"
    tmpPath.contentAsString shouldBe "A different Lorem Ipsum"
  }

  it should "throw exception if applied to a directory" in {
    mkdirs(tmpPath)
    tmpPath should be a directory
    intercept[IOException] {
      tmpPath < "Lorem Ipsum"
    }
  }

  "<<" should "append string to the end of existing file" in {
    tmpPath < "Lorem" << " Ipsum"
    tmpPath.contentAsString shouldBe "Lorem Ipsum"
  }

  it should "create the file if it does not exist" in {
    tmpPath << "Lorem Ipsum"
    tmpPath should exist
    tmpPath.contentAsString shouldBe "Lorem Ipsum"
  }

  it should "throw exception if applied to a directory" in {
    mkdirs(tmpPath)
    tmpPath should be a directory
    intercept[IOException] {
      tmpPath << "Lorem Ipsum"
    }
  }
}
