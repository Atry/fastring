publish / skip := true

enablePlugins(JmhPlugin)

resolvers += Resolver.jcenterRepo

libraryDependencies ++= {
  scalaBinaryVersion.value match {
    case "2.12" =>
      Seq(
        "com.sizmek.fsi" %% "macros" % "0.2.0",
        "io.gatling" % "gatling-charts" % "3.7.4"
      )
    case _ =>
      Nil
  }
}

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
