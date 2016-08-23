
import sbtassembly.AssemblyPlugin.defaultShellScript

lazy val root = (project in file(".")).
  settings(
    name := "tst",
    version := "1.0")
  .settings(
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(prependShellScript = Some(defaultShellScript)),

    assemblyJarName in assembly := s"${name.value}-${version.value}",

    mainClass in assembly := Some("tst.Main")
  )

//    scalaVersion := "2.11.6",
//    libraryDependencies += "com.lihaoyi" %% "ammonite-ops" % "0.3.2",
    // http://lihaoyi.github.io/Ammonite/#Ammonite-REPL
    // libraryDependencies += "com.lihaoyi" % "ammonite-repl" % "0.3.2", // % "test" cross  CrossVersion.full,
    // http://mvnrepository.com/artifact/com.lihaoyi/ammonite-repl_2.11.6/0.3.2
//    libraryDependencies += "com.lihaoyi" % "ammonite-repl_2.11.6" % "0.3.2",
//    libraryDependencies += "com.github.scopt" %% "scopt" % "3.3.0",
//    resolvers += Resolver.sonatypeRepo("public"),
    // sbt test:console
//    initialCommands in (Test, console) := """ammonite.repl.Repl.run("")""",
//	  mainClass in Compile := Some("odd.Test")
//  )
//.
//  settings(
//    assemblyOption in assembly := (assemblyOption in assembly).value.copy(prependShellScript = Some(sbtassembly.AssemblyPlugin.defaultShellScript)),
//    mainClass in assembly := None,
//    assemblyJarName in assembly := s"${name.value}"
//  )
