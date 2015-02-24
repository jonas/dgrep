package dgrep

object Strings {

  def splitToWords(line: String): Array[String] =
    line split "[\\s]+" filter (!_.isEmpty)

}
