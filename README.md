# DGrep

Given a word and a directory, find all files which are descendants of
the directory path and contain the word. Note that only exact words will
match, for example the search word `Scala` will not match the line:
```
Written in Scala.
```
since it is broken down into the words: `Written`, `in`, `Scala.`

## Getting started

To run the tests:

    $ sbt test

## Licensing

This program is licensed under the Apache License, version 2. See [the
LICENSE file](LICENSE) for details.

Certain test data is licensed under separate licenses as indicated in
the README files at the root of each directory:

* [Wikipedia documents](src/test/resources/wikipedia/)
* [Twitter's Effective Scala](src/test/resources/effective-scala/)
