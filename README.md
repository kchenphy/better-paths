# better-paths
Simple and intuitive Hadoop Paths
[![Build Status](https://travis-ci.org/kchenphy/better-paths.svg?branch=master)](https://travis-ci.org/kchenphy/better-paths)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/f54f8123f54c4426ba6bf32afd483604)](https://www.codacy.com/app/kchenphy/better-paths?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=kchenphy/better-paths&amp;utm_campaign=Badge_Grade)


-------
Build Path

```scala
import org.apache.hadoop.fs.Path
import better_paths._
import PathBuilder._

val p1: Path = p"/$user/$folder/$filename.txt"                   // using path interpolator
val p2: Path = new Path("john") / "Documents" / "folder1"        // using / to build path
val p3: Path = new Path("john") / "Documents" / 'folder2         // also works with symbol 
val p4: Path = home / "Documents" / 'folder2 / `..` / `.`        // using home, . and ..
val p5: Array[Path] = home / "Documents" / `*`                   // list every path
```
