name := "TestContainers-Scala-Example-1"

version := "1.0.0-RELEASE"

scalaVersion := "2.12.11"

resolvers ++= Seq(
  Resolver.jcenterRepo,
  Resolver.typesafeRepo("releases")
)

libraryDependencies ++= Seq(
  "redis.clients" % "jedis" % "3.3.0",

  "org.scalatest" %% "scalatest" % "3.0.8" % Test,
  "com.dimafeng" %% "testcontainers-scala-scalatest" % "0.37.0" % Test
)

// for debugging sbt problems
logLevel := Level.Debug

scalacOptions ++= Seq("-unchecked", "-deprecation")