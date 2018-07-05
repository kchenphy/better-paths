package better_paths.common

import org.apache.hadoop.fs.Path
import org.scalatest.{BeforeAndAfterEach, Suite}

trait TempPathProvider extends BeforeAndAfterEach {
  self: Suite =>
  var tmpPath: Path = _

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    tmpPath =
      new Path(System.getProperty("java.io.tmpdir"), System.nanoTime().toString)
  }
}
