/*
 * Copyright 2012 杨博 (Yang Bo)
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

package com.dongxiguo.fastring.benchmark

import com.dongxiguo.fastring.Fastring.Implicits._
import org.openjdk.jmh.annotations.{Benchmark, Param, Scope, State}

/** @see
  *   This benchmark is copied from
  */
@State(Scope.Benchmark)
class FastringBenchmark {

  @Param(
    Array(
      "42", // A small integer in java.lang.Integer.IntegerCache
      "999999" // A large interger that does not cache
    )
  )
  var a: Int = _

  @Benchmark
  final def sb() = {
    val sb: StringBuilder = new StringBuilder
    sb ++= "head "
    var first = true
    for (j <- 0 until 10 view) {
      if (first) {
        first = false
      } else {
        sb ++= "<hr/>"
      }
      sb ++= "baz" ++= j.toString ++= " " ++= a.toString ++= " foo ";
      {
        var first = true
        for (i <- 0 until 4 view) {
          if (first) {
            first = false
          } else {
            sb ++= ","
          }
          sb ++= a.toString
          sb ++= " i="
          sb ++= i.toString
        }
      }
      sb ++= " bar\n"
    }
    sb ++= " tail"
    sb
  }

  @Benchmark
  final def s() =
    s"head ${(for (j <- 0 until 10 view) yield {
        s"baz$j $a foo ${(for (i <- 0 until 4 view) yield {
            s"$a i=$i"
          }).mkString(",")} bar\n"
      }).mkString("<hr/>")} tail"

  @Benchmark
  final def f() =
    f"head ${(for (j <- 0 until 10 view) yield {
        f"baz$j $a foo ${(for (i <- 0 until 4 view) yield {
            f"$a i=$i"
          }).mkString(",")} bar\n"
      }).mkString("<hr/>")} tail"

  @Benchmark
  final def fast() =
    fast"head ${(for (j <- 0 until 10 view) yield {
        fast"baz$j $a foo ${(for (i <- 0 until 4 view) yield {
            fast"$a i=$i"
          }).mkFastring(",")} bar\n"
      }).mkFastring("<hr/>")} tail".toString

}
