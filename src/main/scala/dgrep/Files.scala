package dgrep

import java.io.File

object Files {

  def listDescendantDirectories(root: File): Stream[File] =
    if (!root.exists)
      Stream.empty
    else
      Stream.cons(root, {
        /*
         * If the root directory was deleted before the children have been
         * processed `listFiles` may return null.
         */
        Option(root.listFiles) map { listOfFiles =>
          val subDirs = Stream(listOfFiles: _*).filter(_.isDirectory)
          subDirs.flatMap(listDescendantDirectories)
        } getOrElse Stream.empty
      })

}
