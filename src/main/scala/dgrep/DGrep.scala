package dgrep

import scala.language.postfixOps
import java.io.File
import scala.util.{Try,Success,Failure}
import dgrep.Files._
import dgrep.Strings._

object DGrep {

  val USAGE = "Usage: dgrep <word> <directory>"

  /* Models the basic environment that the program uses for communicating
   * its result. Allows tests to more easily capture the behavior. */
  case class Env(
    stdout: Any => Unit = System.out.println,
    stderr: Any => Unit = System.err.println,
    exit:   Int => Unit = System.exit)

  /* Holds the line number and line content for each matched line. */
  type LineInfo = (Int, String)

  /* Holds the visited file and matched lines or error info. */
  type FileInfo = (File, Try[Stream[LineInfo]])

  def main(args: Array[String]): Unit = {
    val (word, root) = parseArgs(args)

    def toFileInfo(file: File): FileInfo =
      (file, Try { lineInfoForWord(file) filter linesContaining(word) })

    listAllDescendantFiles(root) map toFileInfo foreach {
      case (file, Success(lineInfo)) if lineInfo nonEmpty => println(file)
      case _ =>
    }
  }

  /* Annotates each read line with a line number. */
  def lineInfoForWord(file: File): Stream[LineInfo] =
    (Stream.from(1) zip readFileLines(file))

  def linesContaining(word: String)(lineInfo: LineInfo): Boolean =
    stringContainsWord(word)(lineInfo._2)

  def parseArgs(args: Array[String]): (String, File) = {
    if (args.length != 2)
      usage()

    val word = args(0)
    val root = new File(args(1))
    if (!root.exists || !root.isDirectory)
      usage(s"$root is not a directory")

    (word, root)
  }

  def usage(msgs: String*): Unit = {
    msgs foreach System.err.println
    System.err.println(USAGE)
    System.exit(1)
  }
}
