# DGrep

Given a word and a directory, find all files which are descendants of
the directory path and contain the word. Note that only exact words will
match, for example the search word `Scala` will not match the line:
```
Written in Scala.
```
since it is broken down into the words: `Written`, `in`, `Scala.`

DGrep assumes that all files are UTF-8 encoded.

## Getting started

To build the program:

    $ sbt assembly

This will produce a fat-jar in `target/scala-2.11/dgrep.jar`.

To run the program from the root of the DGrep project:

    $ java -jar target/scala-2.11/dgrep.jar Scala src/test/resources/wikipedia/prog/lang
    src/test/resources/wikipedia/prog/lang/scala.da.txt
    src/test/resources/wikipedia/prog/lang/scala.fr.txt
    src/test/resources/wikipedia/prog/lang/scala.txt

This also shows an example output of the program.

To run the tests:

    $ sbt test

## Options

The program supports the following options:

 * `-s` Suppress error messages printed to stderr.
 * `-L` Print only files not containing the search word.

## Licensing

This program is licensed under the Apache License, version 2. See [the
LICENSE file](LICENSE) for details.

Certain test data is licensed under separate licenses as indicated in
the README files at the root of each directory:

* [Wikipedia documents](src/test/resources/wikipedia/)
* [Twitter's Effective Scala](src/test/resources/effective-scala/)
