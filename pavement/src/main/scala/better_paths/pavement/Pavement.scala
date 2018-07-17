package better_paths.pavement

trait Pavement
    extends QualifiedByFileSystem
    with ExistInFileSystem
    with PathPropertyMatcher
    with PathEmptiness
    with PathContaining

object Pavement extends Pavement
