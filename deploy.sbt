enablePlugins(Travis)

enablePlugins(SonatypeRelease)

lazy val secret = project settings(publishArtifact := false) configure { secret =>
  sys.env.get("SECRET_GIST") match {
    case Some(gist) =>
      secret.addSbtFilesFromGit(gist, file("secret.sbt"))
    case None =>
      secret
  }
}
