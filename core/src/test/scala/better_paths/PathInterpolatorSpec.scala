package better_paths

import org.apache.hadoop.fs.Path
import org.scalatest.{FlatSpec, Matchers}

class PathInterpolatorSpec extends FlatSpec with Matchers {

  "p interpolator" should "build a path" in {
    val user = "JohnDoe"
    val id = 123
    val filename = "my_file"
    val path = p"/$user/$id/$filename.txt"

    path shouldBe a[Path]
    path.toString shouldBe "/JohnDoe/123/my_file.txt"
  }
}
