name := "dgrep"
version := "1.0"
scalaVersion := "2.11.5"
scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

mainClass in assembly := Some("dgrep.DGrep")
assemblyJarName in assembly := "dgrep.jar"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"
