package dgrep

import org.scalatest._
import java.io.{PrintWriter, StringWriter}
import java.util.concurrent.atomic.AtomicInteger

class DGrepSpec extends FlatSpec with Matchers {

  import DGrep._

  class ExitCalledException extends RuntimeException

  case class Context() {
    def stdout = stdoutWriter.toString
    def stderr = stderrWriter.toString
    def exitCode = exitCodeHolder.get

    private val stdoutWriter = new StringWriter
    private val stderrWriter = new StringWriter
    private val exitCodeHolder = new AtomicInteger(0)
    private def exit(code: Int): Unit = {
      exitCodeHolder.set(code)
      throw new ExitCalledException
    }

    val env = Env(
      new PrintWriter(stdoutWriter).println,
      new PrintWriter(stderrWriter).println,
      exit)
  }

  "run" should "fail when no arguments are given" in {
    val context = Context()

    an [ExitCalledException] should be thrownBy {
      DGrep.run(Array())(context.env)
    }

    context.exitCode should be (1)
    context.stdout should be ("")
    context.stderr should be (s"$USAGE\n")
  }

  it should "fail when directory is invalid" in {
    val context = Context()

    an [ExitCalledException] should be thrownBy {
      DGrep.run(Array("word", "some/non-existing/directory"))(context.env)
    }

    context.exitCode should be (1)
    context.stdout should be ("")
    context.stderr should be (s"""some/non-existing/directory is not a directory
                                 |$USAGE
                                 |""".stripMargin)
  }

  it should "find files containing searched word in multi-level directory structure" in {
    val context = Context()
    val args = Array("Odersky", "src/test/resources/wikipedia")

    DGrep.run(args)(context.env)

    context.exitCode should be (0)
    context.stdout should be ("""src/test/resources/wikipedia/prog/lang/scala.da.txt
                                |src/test/resources/wikipedia/prog/lang/scala.fr.txt
                                |src/test/resources/wikipedia/prog/lang/scala.txt
                                |""".stripMargin)
    context.stderr should be ("ERROR:src/test/resources/wikipedia/corrupted/non-utf8-encoded-file.bin:java.nio.charset.MalformedInputException: Input length = 1\n")
  }

  it should "find files containing the searched word in flat directory structure" in {
    val context = Context()
    val args = Array("Twitter", "src/test/resources/effective-scala")

    DGrep.run(args)(context.env)

    context.exitCode should be (0)
    context.stdout should be ("""src/test/resources/effective-scala/effectivescala-cn.mo
                                |src/test/resources/effective-scala/effectivescala-ja.mo
                                |src/test/resources/effective-scala/effectivescala.mo
                                |""".stripMargin)
    context.stderr should be ("")
  }

  it should "not list anything when no files contains the searched word" in {
    val context = Context()
    val args = Array("Fortran", "src/test/resources/effective-scala")

    DGrep.run(args)(context.env)

    context.exitCode should be (0)
    context.stdout should be ("")
    context.stderr should be ("")
  }

  it should "suppress errors when '-s' option is given" in {
    val context = Context()
    val args = Array("-s", "Odersky", "src/test/resources/wikipedia")

    DGrep.run(args)(context.env)

    context.exitCode should be (0)
    context.stderr should be ("")
  }

}
