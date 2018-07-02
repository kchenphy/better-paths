package better_paths

import org.apache.hadoop.fs.Path

class Implicits {

  /** Path interpolations, enabling syntax like:
    *
    * <pre>
    * val user = "john.doe"
    * val p = p"/${user}/file.txt"
    * </pre>
    *
    * @param sc underlying [[StringContext]] instance.
    */
  implicit class PathInterpolations(sc: StringContext) {
    def p(args: Any*): Path = new Path(sc.s(args: _*))
  }
}
