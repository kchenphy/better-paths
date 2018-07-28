package better_paths.path_sugar

trait PathSugar
    extends QualifiedByFileSystem
    with ExistInFileSystem
    with PathPropertyMatcher
    with PathEmptiness
    with PathContaining
    with PathLength

object PathSugar extends PathSugar
