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
}
