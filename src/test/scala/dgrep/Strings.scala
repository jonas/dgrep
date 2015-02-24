package dgrep

import org.scalatest._

class StringsSpec extends FlatSpec with Matchers {

  import Strings._

  "splitToWords" should "split a line with one or more words" in {
    splitToWords("a b c d") should contain allOf ("a", "b", "c", "d")
    splitToWords("one-word") should contain only ("one-word")
  }

  it should "handle multiple spaces between words" in {
    splitToWords(" 	multiple  spaces  ") should contain allOf ("multiple", "spaces")
  }

  it should "return empty result when no words" in {
    splitToWords(" 	 ") shouldBe empty
  }

}
