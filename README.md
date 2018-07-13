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
implicit val fs: FileSystem = myFileSystem                       // assuming an implicit file system defined

val p1: Path = p"/$user/$folder/$filename.txt"                   // using path interpolator
val p2: Path = new Path("john") / "Documents" / "folder1"        // using / to build path
val p3: Path = new Path("john") / "Documents" / 'folder2         // also works with symbol 
val p4: Path = home / "Documents" / 'folder2 / `..` / `.`        // using home, . and ..
val p5: Array[Path] = home / "Documents" / `*`                   // list every path
```

-------
Structure

```scala
p.parent                                                         // get the parent
p.children                                                       // list every path under p, same as p / `*`

```

-------
Glob

```scala
val p = p"a/*/b"
p.globDirectories                                                // glob all directories
p.globFiles                                                      // glob all files
```

-------
Name operations

```scala
val p: Path = p"a/b/c"
assert(p.basename == "c")                                        // get basename
assert(p.qualified == "localhost:port://a/b/c")                  // get qualified by filesystem
```


-------
Read and Write

```scala
p.lines()                                                         // get Seq[String] for all lines
p.lineIterator()                                                  // get an iterator of String
p.contentAsString()                                               // return everything concatenated
p < "lorem ipsum"                                                 // overwrite
p << "lorem ipsum" << "\n" << "dolor sit amet"                    // append
```


------
Attribute
```scala
p.isFile                                                          // is it a file
p.isDirectory                                                     // or directory
p.isSymlink                                                       // or symlink?
p.exists                                                          // does it exist?
p.status                                                          // what is the FileStatus?
p.length                                                          // and what is the length?
```

# `PathSugar`
In addition, `PathSguar` is provided to make `scalatest` with Hadoop Path easier.

## qualified and qualifiedBy
```scala
import better_paths.PathSugar._

val simplified = "a/b"
val full = "http://namenode:port/a/b"
(simplified should be full) (after being qualified)
(simplified should be full) (after being qualifiedBy(fs))
```

## existence
```scala
val path = "a/b"
touch(path)
path should exist
```

## empty
```scala
val path = "a/b"
path should be empty
```


## type check
```scala
val filePath = "a/b"
val dirPath = "a/c"
touch(filePath)
mkdirs(dirPath)

filePath shouldBe a file
dirPath shouldBe a directory
```
