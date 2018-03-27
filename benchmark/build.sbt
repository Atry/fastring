publishArtifact := false

enablePlugins(JmhPlugin)

resolvers += Resolver.jcenterRepo

libraryDependencies ++= {
  scalaBinaryVersion.value match {
    case "2.12" =>
      Seq(
        "com.sizmek.fsi" %% "macros" % "0.1.0",
        "io.gatling" % "gatling-charts" % "2.3.1"
      )
    case _ =>
      Nil
  }
}

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
