package better_paths.pavement

trait Pavement
    extends QualifiedByFileSystem
    with ExistInFileSystem
    with PathPropertyMatcher
    with PathEmptiness
    with PathContaining
    with PathLength

object Pavement extends Pavement
