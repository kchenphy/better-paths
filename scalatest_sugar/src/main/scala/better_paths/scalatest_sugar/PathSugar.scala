package better_paths.scalatest_sugar

trait PathSugar
    extends QualifiedByFileSystem
    with ExistInFileSystem
    with PathPropertyMatcher
    with PathEmptiness
object PathSugar extends PathSugar
