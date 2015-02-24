package dgrep

import scala.language.postfixOps
import org.scalatest._

import java.io.File
import java.io.FileNotFoundException

import dgrep.Strings._

class FilesSpec extends FlatSpec with Matchers {

  import Files._

  val TEST_DATA_ROOT = "src/test/resources"

  case class TestData(name: String, root: String = TEST_DATA_ROOT) {
    val path = root + "/" + name
    val dir = new File(path)
    def stripPath(file: File): String = file.getPath.replace(path, ".")
    def /(name: String): File = new File(dir, name)
  }

  val wikipedia = TestData("wikipedia")
  val wikipediaProg = TestData("wikipedia/prog")
  val wikipediaProgLang = TestData("wikipedia/prog/lang")
  val effectiveScala = TestData("effective-scala")
  val nonExistent = TestData("non-existent-directory")
  val emptyDirectory = TestData("empty-directory", "target")

  "test setup" should "complete with success" in {
    emptyDirectory.dir.mkdir()
    emptyDirectory.dir.exists should be (true)

    nonExistent.dir.exists should be (false)
  }

  "listDescendantDirectories" should "find all sub-directories in multi-level directory structure" in {
    val dirs = listDescendantDirectories(wikipedia.dir)

    dirs.map(wikipedia.stripPath) should contain allOf (
      ".",
      "./canada",
      "./prog",
      "./prog/lang"
    )
  }

  it should "find no sub-directories in flat directory structure" in {
    val dirs = listDescendantDirectories(effectiveScala.dir)

    dirs.map(effectiveScala.stripPath) should contain only (".")
  }

  it should "find no sub-directories in empty directory structure" in {
    val dirs = listDescendantDirectories(emptyDirectory.dir)

    dirs.map(emptyDirectory.stripPath) should contain only (".")
  }

  it should "find no directories for non existent directory" in {
    val dirs = listDescendantDirectories(nonExistent.dir)

    dirs.map(nonExistent.stripPath) shouldBe empty
  }

  "listDirectoryFiles" should "find no files in a non existent directory" in {
    val files = listDirectoryFiles(nonExistent.dir)

    files.map(nonExistent.stripPath) shouldBe empty
  }

  it should "find no files in am empty directory" in {
    val files = listDirectoryFiles(emptyDirectory.dir)

    files.map(emptyDirectory.stripPath) shouldBe empty
  }

  it should "find all files in a multi-level directory structure" in {
    val rootFiles = listDirectoryFiles(wikipedia.dir)
    rootFiles.map(wikipedia.stripPath) should contain only ("./README.md")

    val progLangFiles = listDirectoryFiles(wikipediaProgLang.dir)
    progLangFiles.map(wikipediaProgLang.stripPath) should contain allOf (
      "./haskell.txt",
      "./scala.da.txt",
      "./scala.fr.txt",
      "./scala.txt"
    )
  }

  it should "find all files in a flat directory structure" in {
    val files = listDirectoryFiles(effectiveScala.dir)

    files.map(effectiveScala.stripPath) should contain allOf (
      "./effectivescala-cn.mo",
      "./effectivescala-ja.mo",
      "./effectivescala.mo",
      "./README.md"
    )
  }

  "listAllDescendantFiles" should "find no files in a non existent directory" in {
    val files = listAllDescendantFiles(nonExistent.dir)

    files.map(nonExistent.stripPath) shouldBe empty
  }

  it should "find no files in am empty directory" in {
    val files = listAllDescendantFiles(emptyDirectory.dir)

    files.map(emptyDirectory.stripPath) shouldBe empty
  }

  it should "find all files in a multi-level directory structure" in {
    val rootFiles = listAllDescendantFiles(wikipedia.dir)

    rootFiles.map(wikipedia.stripPath) should contain allOf (
      "./README.md",
      "./canada/constitution.fr.txt",
      "./canada/constitution.txt",
      "./canada/montreal.ar.txt",
      "./canada/montreal.txt",
      "./canada/quebec.da.txt",
      "./canada/quebec.fr.txt",
      "./corrupted/non-utf8-encoded-file.bin",
      "./prog/computer-science.da.txt",
      "./prog/computer-science.txt",
      "./prog/functional.da.txt",
      "./prog/functional.txt",
      "./prog/lang/haskell.txt",
      "./prog/lang/scala.da.txt",
      "./prog/lang/scala.fr.txt",
      "./prog/lang/scala.txt"
    )
  }

  it should "find all files in a flat directory structure" in {
    val files = listAllDescendantFiles(effectiveScala.dir)

    files.map(effectiveScala.stripPath) should contain allOf (
      "./effectivescala-cn.mo",
      "./effectivescala-ja.mo",
      "./effectivescala.mo",
      "./README.md"
    )
  }

  "readFileLines" should "iterate over all content in a file" in {
    val content = readFileLines(wikipediaProg / "computer-science.da.txt") mkString("", "\n", "\n")
    val shaBytes = java.security.MessageDigest.getInstance("SHA") digest content.getBytes("UTF-8")
    val shaString = shaBytes map ("%02x" format _) mkString

    shaString should be ("02f24edf13271c31fde5f7bd4cd9e1a2bd51349b")
  }

  it should "be composable with stringContainsWord" in {
    def fileContains(file: File, word: String): Boolean =
      readFileLines(file) exists stringContainsWord(word)

    fileContains(wikipediaProgLang / "scala.txt", "Scala") should be (true)
    fileContains(wikipediaProgLang / "scala.da.txt", "programmering") should be (true)
    fileContains(wikipediaProgLang / "scala.da.txt", "målestoksforhold.") should be (true)

    fileContains(effectiveScala / "effectivescala.mo", "Twitter") should be (true)
    fileContains(effectiveScala / "effectivescala-ja.mo", "の") should be (true)

    fileContains(wikipediaProgLang / "scala.txt", "målestoksforhold") should be (false)
  }

  it should "report file not found for non existent files" in {
    an [FileNotFoundException] should be thrownBy readFileLines(nonExistent / "non-existent-file")
    an [FileNotFoundException] should be thrownBy readFileLines(emptyDirectory / "non-existent-file")
  }

  it should "report file not found when given a directory" in {
    an [FileNotFoundException] should be thrownBy readFileLines(emptyDirectory.dir)
  }

  it should "report encoding error when reading file that is not UTF-8 encoded" in {
    an [java.nio.charset.MalformedInputException] should be thrownBy {
      readFileLines(wikipedia / "corrupted/non-utf8-encoded-file.bin") contains "word"
    }
  }

}
