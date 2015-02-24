package dgrep

import org.scalatest._

class StringsSpec extends FlatSpec with Matchers {

  import Strings._

  "stringContainsWord" should "find word a string containing the word" in {
    stringContainsWord("a")("a b c d") should be (true)
    stringContainsWord("one-word")("one-word") should be (true)
    stringContainsWord("last.")("first and	last.") should be (true)
  }

  it should "find no words in a string without the word" in {
    stringContainsWord("not-found")("a b c d") should be (false)
    stringContainsWord("not-found")("first and last.") should be (false)
  }

  it should "find no words in a string without words" in {
    stringContainsWord("not-found")("") should be (false)
    stringContainsWord("not-found")(" 	") should be (false)
  }

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
