package dgrep

import scala.language.postfixOps
import java.io.File
import scala.util.{Try,Success,Failure}
import scala.collection.mutable.ArrayBuffer
import dgrep.Files._
import dgrep.Strings._

object DGrep {

  val USAGE = "Usage: dgrep [-s] <word> <directory>"

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

  def main(args: Array[String]): Unit =
    run(args)(Env())

  def run(args: Array[String])(implicit env: Env): Unit = {
    val (flags, word, root) = parseArgs(args)

    val printers = ArrayBuffer(Printers.printFileNameWithMatches)
    if (!(flags contains "-s"))
      printers += Printers.printError

    def toFileInfo(file: File): FileInfo =
      (file, Try { lineInfoForWord(file) filter linesContaining(word) })

    val printer = printers.foldRight(Printers.dontPrint)((p1, p2) => p1(env) orElse p2)

    listAllDescendantFiles(root) map toFileInfo foreach printer
  }

  object Printers {
    type Printer = PartialFunction[FileInfo, Unit]

    val printFileNameWithMatches: Env => Printer = env => {
      case (file, Success(lineInfo)) if lineInfo nonEmpty =>
        env.stdout(file)
    }

    val printError: Env => Printer = env => {
      case (file, Failure(x)) =>
        env.stderr(s"ERROR:$file:$x")
    }

    val dontPrint: Printer = { case _ => }
  }

  /* Annotates each read line with a line number. */
  def lineInfoForWord(file: File): Stream[LineInfo] =
    (Stream.from(1) zip readFileLines(file))

  def linesContaining(word: String)(lineInfo: LineInfo): Boolean =
    stringContainsWord(word)(lineInfo._2)

  def parseArgs(args: Array[String])(implicit env: Env): (Array[String], String, File) = {
    if (args.length < 2)
      usage()

    val (flags, Array(word, path)) = args.splitAt(args.length - 2)

    val root = new File(path)
    if (!root.exists || !root.isDirectory)
      usage(s"$root is not a directory")

    (flags, word, root)
  }

  def usage(msgs: String*)(implicit env: Env): Unit = {
    msgs foreach env.stderr
    env.stderr(USAGE)
    env.exit(1)
  }
}
