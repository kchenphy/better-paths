import java.nio.charset.{Charset, StandardCharsets}

import org.apache.commons.io.IOUtils
import org.apache.hadoop.fs.{FileSystem, Path}

package object better_paths extends Implicits {

  implicit class RichPath(val path: Path)(implicit fs: FileSystem) {

    def contentAsString(implicit charset: Charset = StandardCharsets.UTF_8): String =
      IOUtils.toString(fs.open(path), charset)
  }
}
