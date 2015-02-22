package dgrep

import java.io.File

object Files {

  def listDescendantDirectories(root: File): Stream[File] =
    Stream.cons(root, {
      val subDirs = Stream(root.listFiles: _*).filter(_.isDirectory)
      subDirs.flatMap(listDescendantDirectories)
    })

}
