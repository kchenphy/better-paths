package better_paths

import org.apache.hadoop.fs.Path
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

class PathBuilderSpec extends FlatSpec with Matchers with TableDrivenPropertyChecks {
  "path" can "be built using /" in {
    val expected = p"a/b"

    val testCases = Table(
      ("parent", "child"),
      ("a", "b"),
      ("a/", "b")
    )

    forAll(testCases) {
      (parent, child) => new Path(parent) / child shouldBe expected
    }
  }
}
