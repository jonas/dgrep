package dgrep

import java.io.File

object Files {

  def listDescendantDirectories(root: File): Stream[File] =
    if (!root.exists)
      Stream.empty
    else
      Stream.cons(root, {
        listFiles(root) filter (_.isDirectory) flatMap listDescendantDirectories
      })

  def listDirectoryFiles(dir: File): Stream[File] =
    listFiles(dir) filter (!_.isDirectory)

  /*
   * List directory files and gracefully handle the case where the
   * passed directory does not exist or is not a direcoty in which
   * case `listFiles` returns null. This can occur when this method
   * is used in the construction of a stream and a directory is
   * deleted before it has been visited.
   *
   * Furthermore, ensure that files are listed in alphabetical order
   * to ensure uniform behavior across different OSes.
   */
  def listFiles(dir: File): Stream[File] =
    Option(dir.listFiles) map { listOfFiles =>
      Stream((listOfFiles sortBy (_.getPath)): _*)
    } getOrElse {
      Stream.empty
    }

}
