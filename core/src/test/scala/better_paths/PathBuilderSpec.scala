package better_paths

import org.apache.hadoop.fs.Path
import org.scalatest.{FlatSpec, Matchers}

class PathBuilderSpec extends FlatSpec with Matchers {

  "/" can "be used with strings" in {
    new Path("a") / "b" shouldBe new Path("a/b")
  }

  it should "also work with symbols" in {
    new Path("a") / 'b shouldBe new Path("a/b")
  }

  it should "ignore parent's trailing slash" in {
    new Path("a/") / "b" shouldBe new Path("a/b")
  }
}
