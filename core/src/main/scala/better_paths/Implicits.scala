package better_paths

import org.apache.hadoop.fs.Path

trait Implicits
    extends PathBuilder.Implicits
    with PathGlob.Implicits
    with PathContent.Implicits
    with PathAttribute.Implicits
    with PathStructure.Implicits
    with PathName.Implicits {

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
