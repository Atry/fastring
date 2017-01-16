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

final object FastringBenchmark {

  @volatile
  var result: Any = ""

  final def sb(sb: StringBuilder, a: Int) = {
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

  final def s(a: Int) =
    s"head ${
      (for (j <- 0 until 10 view) yield {
        s"baz$j $a foo ${
          (for (i <- 0 until 4 view) yield {
            s"$a i=$i"
          }).mkString(",")
        } bar\n"
      }).mkString("<hr/>")
    } tail"

  final def f(a: Int) =
    f"head ${
      (for (j <- 0 until 10 view) yield {
        f"baz$j $a foo ${
          (for (i <- 0 until 4 view) yield {
            f"$a i=$i"
          }).mkString(",")
        } bar\n"
      }).mkString("<hr/>")
    } tail"

  final def fast(a: Int) =
    fast"head ${
      (for (j <- 0 until 10 view) yield {
        fast"baz$j $a foo ${
          (for (i <- 0 until 4 view) yield {
            fast"$a i=$i"
          }).mkFastring(",")
        } bar\n"
      }).mkFastring("<hr/>")
    } tail"

  final def test1() {
    val start = System.nanoTime()

    for (i <- 0 until 100) {
      for (j <- 0 until 1000) {
        result = fast(j).toString
      }
    }

    println(fast"     Fastring: ${(System.nanoTime() - start).leftPad(10)}")
  }

  final def test4() {
    val start = System.nanoTime()

    for (i <- 0 until 100) {
      for (j <- 0 until 1000) {
        result = f(j)
      }
    }

    println(fast"            f: ${(System.nanoTime() - start).leftPad(10)}")
  }

  final def test2() {
    val start = System.nanoTime()

    for (i <- 0 until 100) {
      for (j <- 0 until 1000) {
        result = s(j)
      }
    }

    println(fast"            s: ${(System.nanoTime() - start).leftPad(10)}")
  }

  final def test3() {
    val start = System.nanoTime()

    for (i <- 0 until 100) {
      for (j <- 0 until 1000) {
        result = sb(new StringBuilder, j).toString
      }
    }

    println(fast"StringBuilder: ${(System.nanoTime() - start).leftPad(10)}")
  }

  final def main(args: Array[String]) {
    println("length:" + s(999).length)
    println()

    for (i <- 0 until 11) {
      System.gc()
      test1()

      System.gc()
      test2()

      System.gc()
      test3()

      System.gc()
      test4()

      println()
    }
  }
}