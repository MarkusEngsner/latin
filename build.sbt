name := "latin"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "2.2.0"
libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "42.2.8",
  "io.getquill" %% "quill-jdbc" % "3.6.0-RC3",
  "com.opentable.components" % "otj-pg-embedded" % "0.13.3"
)

// for logging errors from quote
libraryDependencies ++= Seq("org.slf4j" % "slf4j-api" % "1.7.5",
  "org.slf4j" % "slf4j-simple" % "1.7.5")
