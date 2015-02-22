package dgrep

import org.scalatest._
import java.io.File

class FilesSpec extends FlatSpec with Matchers {

  import Files._

  val TEST_DATA_ROOT = "src/test/resources"

  case class TestData(name: String, root: String = TEST_DATA_ROOT) {
    val path = root + "/" + name
    val dir = new File(path)
    def stripPath(file: File): String = file.getPath.replace(path, ".")
  }

  val wikipedia = TestData("wikipedia")
  val effectiveScala = TestData("effective-scala")

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
    val empty = TestData("empty-directory", "target")

    empty.dir.mkdir()
    empty.dir.exists should be (true)

    val dirs = listDescendantDirectories(empty.dir)

    dirs.map(empty.stripPath) should contain only (".")
  }

  it should "find no directories for non existent directory" in {
    val nonExistent = TestData("non-existent-directory")

    val dirs = listDescendantDirectories(nonExistent.dir)

    dirs.map(nonExistent.stripPath) shouldBe empty
  }
}
