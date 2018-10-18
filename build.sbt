name := """methode-635-api"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.6"

crossScalaVersions := Seq("2.11.12", "2.12.4")

libraryDependencies += guice

// Swagger Documentation
libraryDependencies += "io.swagger" %% "swagger-play2" % "1.6.0"

// JWT
libraryDependencies += "com.auth0" % "java-jwt" % "3.2.0"

// MongoDB async driver
libraryDependencies += "org.mongodb" % "mongodb-driver-async" % "3.8.0"