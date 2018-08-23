import sbt._

lazy val `avro-playground` = (project in file("."))
  .settings(

    organization := "com.github.filosganga",
    name := "avro-playground",
    description := "Avro Playground",

    homepage := Some(url("https://github.com/ovotech/avro-playground")),
    organizationHomepage := Some(url("https://filippodeluca.com/")),
    startYear := Some(2018),
    licenses := Seq(("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))),

    scmInfo := Some(
      ScmInfo(
        url("https://github.com/ovotech/avro-playground"),
        "git@github.com:ovotech/avro-playground.git"
      )
    ),

    scalaVersion := "2.12.6",
    
    libraryDependencies ++= Seq(
      "org.apache.avro" % "avro" % "1.8.2",
      "com.sksamuel.avro4s" %% "avro4s-core" % "1.9.0",
      "com.sksamuel.avro4s" %% "avro4s-macros" % "1.9.0",
      "org.scalacheck" %% "scalacheck" % "1.14.0" % Test,
      "org.scalatest" %% "scalatest" % "3.0.5" % Test
    )
  )