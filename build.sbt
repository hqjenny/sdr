name := "chisel3-dsp-module"

version := "1.0"

scalaVersion := "2.11.7"

resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases"))

val defaultVersions = Map(
  "firrtl" -> "1.1-SNAPSHOT",
  "dsptools" -> "1.0",
  "chisel3" -> "3.1-SNAPSHOT",
  "chisel-iotesters" -> "1.2-SNAPSHOT")

libraryDependencies ++= (Seq("chisel3","chisel-iotesters","dsptools","firrtl").map {
  dep: String => "edu.berkeley.cs" %% dep % sys.props.getOrElse(dep + "Version", defaultVersions(dep)) })

libraryDependencies += "org.spire-math" %% "spire" % "0.11.0"

libraryDependencies += "org.scalanlp" %% "breeze" % "0.12"

libraryDependencies += "co.theasi" %% "plotly" % "0.1"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.5",
  "org.scalacheck" %% "scalacheck" % "1.12.4")

lazy val recordHash = TaskKey[Unit]("record-chisel3dsp-dependency-hash")

recordHash := {
    import java.io._
    try {
      val hash = sys.env("Chisel3DSPDependenciesCommit")
      val pw = new PrintWriter(new File("Chisel3DSPDependencies.hash"))
      pw.write(hash.toString)
      pw.close
    } catch {
      case e: Exception => println("Run setenv.sh in your Chisel3DSPDependencies directory!")
      System.exit(0)
    } 
}

compile in Compile <<= (compile in Compile).dependsOn(recordHash)


//organization := "edu.berkeley.cs"
// 
// version := "3.0-SNAPSHOT"
// 
// name := "chisel-tutorial"
// 
// scalaVersion := "2.11.7"
// 
// scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-language:                reflectiveCalls")
// 
// // Provide a managed dependency on X if -DXVersion="" is supplied on the command line.
// // The following are the default development versions, not the "release" versions.
// val defaultVersions = Map(
//   "chisel3" -> "3.0-SNAPSHOT",
//   "chisel-iotesters" -> "1.1-SNAPSHOT"
//   )
// libraryDependencies ++= (Seq("chisel3","chisel-iotesters").map {
//   dep: String => "edu.berkeley.cs" %% dep % sys.props.getOrElse(dep + "Version",           defaultVersions(dep)) })
// 
// libraryDependencies += "edu.berkeley.cs" %% "firrtl" % "1.1-SNAPSHOT"
// resolvers ++= Seq(
//   Resolver.sonatypeRepo("snapshots"),
//   Resolver.sonatypeRepo("releases")
// )
// 
// // Recommendations from http://www.scalatest.org/user_guide/using_scalatest_with_sbt
// logBuffered in Test := false
// 
// // Disable parallel execution when running te
// //  Running tests in parallel on Jenkins currently fails.
// parallelExecution in Test := false
//
