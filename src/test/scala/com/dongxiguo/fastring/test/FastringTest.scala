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

package com.dongxiguo.fastring.test

import org.junit.Test
import org.junit.Assert

final class FastringTest {
  @Test
  final def test1() {
    import com.dongxiguo.fastring.Fastring.Implicits._
    val a = 1
    val c = Seq(Seq("sss", "dd"), Seq("asdf"))
    Assert.assertEquals(
      fast"a $a aa ${c.mkFastring(",")}".toString,
      s"a $a aa ${c.mkString(",")}")
  }

  @Test
  final def test2() {
    import com.dongxiguo.fastring.Fastring.Implicits._
    val a = 1

    Assert.assertEquals(
      fast"baz $a foo ${a.filled(5, ' ')} bar".toString,
      f"baz $a foo $a% 5d bar")
  }

  @Test
  final def test3() {
    import com.dongxiguo.fastring.Fastring.Implicits._
    val a = 1

    Assert.assertEquals(
      fast"baz $a foo ${
        fast"inner$a"
      } bar".toString,
      f"baz $a foo inner$a bar")
  }

  @Test
  final def test4() {
    import com.dongxiguo.fastring.Fastring.Implicits._
    val a = 1

    Assert.assertEquals(
      fast"baz $a foo ${
        (for (i <- 0 until 5) yield {
          s"i=$i"
        }).mkFastring
      } bar".toString,
      s"baz $a foo ${
        (for (i <- 0 until 5) yield {
          s"i=$i"
        }).mkString
      } bar")
  }

  @Test
  final def test5() {
    import com.dongxiguo.fastring.Fastring.Implicits._
    def a = 1

    Assert.assertEquals(
      fast"baz $a foo ${
        (for (i <- 0 until 5) yield {
          fast"i=$i"
        }).mkFastring
      } bar".toString,
      s"baz $a foo ${
        (for (i <- 0 until 5) yield {
          s"i=$i"
        }).mkString
      } bar")
  }

  @Test
  final def test6() {
    import com.dongxiguo.fastring.Fastring.Implicits._
    def a = 1

    Assert.assertEquals(
      fast"baz $a foo ${
        (for (i <- 0 until 5) yield {
          fast"i=${(-i).filled(3, '0')}"
        }).mkFastring
      } bar".toString,
      s"baz $a foo ${
        (for (i <- 0 until 5) yield {
          f"i=${-i}%03d"
        }).mkString
      } bar")
  }
}
