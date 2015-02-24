package dgrep

object Strings {

  def stringContainsWord(word: String)(line: String): Boolean =
    splitToWords(line) contains word

  def splitToWords(line: String): Array[String] =
    line split "[\\s]+" filter (!_.isEmpty)

}
