/*
 * Copyright 2012-2017 杨博 (Yang Bo)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

lazy val fastring = crossProject in file(".")

lazy val fastringJS = fastring.js
lazy val fastringJVM = fastring.jvm

lazy val benchmark = project.dependsOn(fastringJVM)

organization in ThisBuild := "com.dongxiguo"

publish / skip := true

Global / parallelExecution := {
  import Ordering.Implicits._
  VersionNumber(scalaVersion.value).numbers >= Seq(2L, 12L)
}
