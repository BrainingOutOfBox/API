name := """methode-635-api"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.6"

crossScalaVersions := Seq("2.11.12", "2.12.4")

libraryDependencies += guice

// Swagger Documentation
// https://mvnrepository.com/artifact/io.swagger/swagger-play2
libraryDependencies += "io.swagger" %% "swagger-play2" % "1.6.0"

// JWT
// https://mvnrepository.com/artifact/com.auth0/java-jwt
libraryDependencies += "com.auth0" % "java-jwt" % "3.2.0"

// MongoDB async driver
// https://mvnrepository.com/artifact/org.mongodb/mongodb-driver-async
libraryDependencies += "org.mongodb" % "mongodb-driver-async" % "3.8.0"

// ModelMapper
// https://mvnrepository.com/artifact/org.modelmapper/modelmapper
libraryDependencies += "org.modelmapper" % "modelmapper" % "2.3.2"

// MarkdownGenerator
// https://mvnrepository.com/artifact/net.steppschuh.markdowngenerator/markdowngenerator
libraryDependencies += "net.steppschuh.markdowngenerator" % "markdowngenerator" % "1.3.1.1"