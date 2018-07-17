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
implicit val fs: FileSystem = myFileSystem                // assuming an implicit file system defined
                                                          
val p1: Path = p"/$user/$folder/$filename.txt"            // using path interpolator
val p2: Path = new Path("john") / "Documents" / "folder1" // using / to build path
val p3: Path = new Path("john") / "Documents" / 'folder2  // also works with symbol 
val p4: Path = home / "Documents" / 'folder2 / `..` / `.` // using home, . and ..
val p5: Array[Path] = home / "Documents" / `*`            // list every path
```                                                       
                                                          
-------                                                   
Structure                                                 
                                                          
```scala                                                  
p.parent                                                  // get the parent
p.children                                                // list every path under p, same as p / `*`
                                                          
```                                                       
                                                          
-------                                                   
Glob                                                      
                                                          
```scala                                                  
val p = p"a/*/b"                                          
p.globDirectories                                         // glob all directories
p.globFiles                                               // glob all files
```                                                       
                                                          
-------                                                   
Name operations                                           
                                                          
```scala                                                  
val p: Path = p"a/b/c"                                    
assert(p.basename == "c")                                 // get basename
assert(p.qualified == "localhost:port://a/b/c")           // get qualified by filesystem
```                                                       
                                                          
                                                          
-------                                                   
Read and Write                                            
                                                          
```scala                                                  
p.lines()                                                 // get Seq[String] for all lines
p.lineIterator()                                          // get an iterator of String
p.contentAsString()                                       // return everything concatenated
p < "lorem ipsum"                                         // overwrite
p << "lorem ipsum" << "\n" << "dolor sit amet"            // append
```                                                       
                                                          
                                                          
------                                                    
Attribute                                                 
```scala                                                  
p.isFile                                                  // is it a file
p.isDirectory                                             // or directory
p.isSymlink                                               // or symlink?
p.exists                                                  // does it exist?
p.status                                                  // what is the FileStatus?
p.length                                                  // and what is the length?
```

## `PathSugar` for writing meaningful/readable unit tests in `scalatest`
In addition, `PathSguar` is provided to make `scalatest` with Hadoop Path easier.

### Qualified and qualifiedBy
```scala
import better_paths.PathSugar._

val unqualified = "a/b"
val full = "http://namenode:port/a/b"
// Naively asserting "simplified should be full" would fail, because "unqualified" is not 
// qualified with protocol, namenode and port.
// "qualified" or "qualifiedBy(fs)" can solve this problem.  
(unqualified should be full) (after being qualified)
(unqualified should be full) (after being qualifiedBy(fs))
```

### Existence
```scala
val path = "a/b"
touch(path)
// reads better than "path.exist shouldBe true"
path should exist
```

### Emptiness
```scala
// assert file is empty
val emptyFile = "a/b"
touch(emptyFile)
emptyFile should be empty

// assert directory is empty (i.e. contains no files)
val emptyDirectory = "c"
mkdir(emptyDirectory)
emptyDirectory should be empty
```

### Type check
```scala
val filePath = "a/b"
val dirPath = "a/c"
touch(filePath)
mkdirs(dirPath)

// assert certain path is a file or directory
// reads better than "filePath.status.isFile shouldBe true"
filePath shouldBe a file
dirPath shouldBe a directory
```
