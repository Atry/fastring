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

package com.dongxiguo.fastring

import language.experimental.macros
import scala.collection.TraversableLike
import scala.collection.mutable.LazyBuilder
import scala.collection.generic.CanBuildFrom
import scala.reflect.macros.Context

abstract class Fastring
    extends TraversableLike[String, Fastring]
    with Traversable[String] { self =>
  override final def seq = this

  override protected final def newBuilder = Fastring.newBuilder

  final def appendTo(appendable: Appendable) {
    for (string <- this) {
      appendable.append(string)
    }
  }

  final def appendTo(stringBuilder: StringBuilder) {
    for (string <- this) {
      stringBuilder ++= string
    }
  }

  override final def mkString = {
    val sb = new java.lang.StringBuilder
    for (s <- this) {
      sb.append(s)
    }
    sb.toString
  }

  override final def toString = mkString

}

final object Fastring {

  final object Empty extends Fastring {
    @inline
    override final def foreach[U](visitor: String => U) {}
  }

  final class FromAny(from: Any) extends Fastring {
    @inline
    override final def foreach[U](visitor: String => U) {
      visitor(String.valueOf(from))
    }
  }

  final class FromCharArray(from: Array[Char]) extends Fastring {
    @inline
    override final def foreach[U](visitor: String => U) {
      visitor(String.valueOf(from))
    }
  }

  final class FromBoolean(from: Boolean) extends Fastring {
    @inline
    override final def foreach[U](visitor: String => U) {
      visitor(String.valueOf(from))
    }
  }

  final class FromByte(from: Byte) extends Fastring {
    @inline
    override final def foreach[U](visitor: String => U) {
      visitor(String.valueOf(from))
    }
  }

  final class FromChar(from: Char) extends Fastring {
    @inline
    override final def foreach[U](visitor: String => U) {
      visitor(String.valueOf(from))
    }
  }

  final class FromShort(from: Short) extends Fastring {
    @inline
    override final def foreach[U](visitor: String => U) {
      visitor(String.valueOf(from))
    }
  }

  final class FromInt(from: Int) extends Fastring {
    @inline
    override final def foreach[U](visitor: String => U) {
      visitor(String.valueOf(from))
    }
  }

  final class FromLong(from: Long) extends Fastring {
    @inline
    override final def foreach[U](visitor: String => U) {
      visitor(String.valueOf(from))
    }
  }

  final class FromFloat(from: Float) extends Fastring {
    @inline
    override final def foreach[U](visitor: String => U) {
      visitor(String.valueOf(from))
    }
  }

  final class FromDouble(from: Double) extends Fastring {
    @inline
    override final def foreach[U](visitor: String => U) {
      visitor(String.valueOf(from))
    }
  }

  final class FromString(string: String) extends Fastring {

    @inline
    override final def foreach[U](visitor: String => U) {
      visitor(string)
    }
  }

  @deprecated(message = "Use `leftPad` instead", since = "0.3.0")
  final class FilledLong(
      value: Long,
      minWidth: Int,
      filledChar: Char,
      radix: Int
  ) extends LeftPadLong(value, minWidth, filledChar, radix)

  class LeftPadLong(value: Long, minWidth: Int, filledChar: Char, radix: Int)
      extends Fastring {
    @inline
    override final def foreach[U](visitor: String => U) {
      val unfilled = java.lang.Long.toString(value, radix)
      if (unfilled.length < minWidth) {
        if (value >= 0) {
          visitor(
            new String(Array.fill(minWidth - unfilled.length)(filledChar))
          )
          visitor(unfilled)
        } else {
          visitor("-")
          visitor(
            new String(Array.fill(minWidth - unfilled.length)(filledChar))
          )
          visitor(unfilled.substring(1))
        }
      } else {
        visitor(unfilled)
      }
    }
  }

  @deprecated(message = "Use `leftPad` instead", since = "0.3.0")
  final class FilledInt(value: Int, minWidth: Int, filledChar: Char, radix: Int)
      extends LeftPadInt(value, minWidth, filledChar, radix)

  class LeftPadInt(value: Int, minWidth: Int, filledChar: Char, radix: Int)
      extends Fastring {
    @inline
    override final def foreach[U](visitor: String => U) {
      val unfilled = java.lang.Integer.toString(value, radix)
      if (unfilled.length < minWidth) {
        if (value >= 0) {
          visitor(
            new String(Array.fill(minWidth - unfilled.length)(filledChar))
          )
          visitor(unfilled)
        } else {
          visitor("-")
          visitor(
            new String(Array.fill(minWidth - unfilled.length)(filledChar))
          )
          visitor(unfilled.substring(1))
        }
      } else {
        visitor(unfilled)
      }
    }
  }

  final def empty = Empty

  @inline
  final def apply[A <: Fastring](fastring: A): A = fastring

  @inline
  final def apply(from: Any) = new FromAny(from)

  @inline
  final def apply(from: Array[Char]) = new FromCharArray(from)

  @inline
  final def apply(from: Boolean) = new FromBoolean(from)

  @inline
  final def apply(from: Byte) = new FromByte(from)

  @inline
  final def apply(from: Char) = new FromChar(from)

  @inline
  final def apply(from: Short) = new FromShort(from)

  @inline
  final def apply(from: Int) = new FromInt(from)

  @inline
  final def apply(from: Long) = new FromLong(from)

  @inline
  final def apply(from: Float) = new FromFloat(from)

  @inline
  final def apply(from: Double) = new FromDouble(from)

  final def applyVarargs_impl(c: Context)(
      argument1: c.Expr[Any],
      argument2: c.Expr[Any],
      rest: c.Expr[Any]*
  ): c.Expr[Fastring] = {
    val visitorExpr =
      c.Expr[String => _](c.universe.Ident(c.universe.newTermName("visitor")))
    val foreachBodyExpr = rest.foldLeft[c.Expr[Unit]](c.universe.reify {
      _root_.com.dongxiguo.fastring
        .Fastring(argument1)
        .foreach(visitorExpr.splice)
      _root_.com.dongxiguo.fastring
        .Fastring(argument2)
        .foreach(visitorExpr.splice)
    }) { (prefixExpr, argument) =>
      c.universe.reify {
        prefixExpr.splice
        _root_.com.dongxiguo.fastring
          .Fastring(argument)
          .foreach(visitorExpr.splice)
      }
    }
    c.universe.reify {
      new _root_.com.dongxiguo.fastring.Fastring {
        @inline
        override final def foreach[U](
            visitor: _root_.scala.Predef.String => U
        ) {
          foreachBodyExpr.splice
        }
      }
    }
  }

  @inline final def apply(
      argument1: Any,
      argument2: Any,
      rest: Any*
  ): Fastring = macro applyVarargs_impl

  @inline
  final def apply(string: String) = new FromString(string)

  object FastringCanBuildFrom extends CanBuildFrom[Fastring, String, Fastring] {
    def apply(from: Fastring) = newBuilder
    def apply() = newBuilder
  }

  @inline
  implicit final def canBuildFrom = FastringCanBuildFrom

  final def newBuilder = new LazyBuilder[String, Fastring] {
    final def result() = {
      val parts = this.parts
      this.parts = null
      new Fastring {
        @inline
        override final def foreach[U](visitor: String => U) {
          for (part <- parts) {
            part.foreach(visitor)
          }
        }
      }
    }
  }

  final object Implicits {
    type Fastring = com.dongxiguo.fastring.Fastring

    object FastringContext {

      private def newLocalFastringClass(
          c: Context,
          escapeFunction: String => String
      )(arguments: Seq[c.Expr[Any]]): c.Expr[Fastring] = {

        import c.universe._
        val Apply(Select(Apply(_, List(Apply(_, partTrees))), _), _) =
          c.macroApplication
        assert(partTrees.length == arguments.length + 1)
        val visitorExpr =
          c.Expr[String => _](c.universe.Ident(newTermName("visitor")))
        val visitPartExprs = for (partTree <- partTrees) yield {
          val Literal(Constant(part: String)) = partTree
          if (part == "") {
            reify(())
          } else {
            val partExpr =
              c.Expr[String](Literal(Constant(escapeFunction(part))))
            reify(visitorExpr.splice(partExpr.splice))
          }
        }
        val visitAllExpr =
          0.until(arguments.length)
            .foldLeft[c.Expr[Unit]](c.Expr(c.universe.EmptyTree)) {
              (prefixExpr, i) =>
                val argumentExpr =
                  c.Expr[Any](c.universe.Ident(newTermName("__arguments" + i)))
                val visitPartExpr = visitPartExprs(i)
                c.universe.reify {
                  prefixExpr.splice
                  visitPartExpr.splice
                  _root_.com.dongxiguo.fastring
                    .Fastring(argumentExpr.splice)
                    .foreach(visitorExpr.splice)
                }
            }
        // Workaround for https://issues.scala-lang.org/browse/SI-6711
        val valDefTrees =
          (for ((argumentExpr, i) <- arguments.iterator.zipWithIndex) yield {
            c.universe.ValDef(
              c.universe.Modifiers(),
              c.universe.newTermName("__arguments" + i),
              c.universe.TypeTree(),
              argumentExpr.tree
            )
          })
        val visitLastPartExpr = visitPartExprs(arguments.length)
        val newFastringExpr =
          c.universe.reify {
            new _root_.com.dongxiguo.fastring.Fastring {
              @inline
              override final def foreach[U](
                  visitor: _root_.scala.Predef.String => U
              ) {
                visitAllExpr.splice
                visitLastPartExpr.splice
              }
            }
          }
        c.Expr(c.universe.Block(valDefTrees.toList, newFastringExpr.tree))
      }

      final def fast_impl(c: Context)(
          arguments: c.Expr[Any]*
      ): c.Expr[Fastring] =
        newLocalFastringClass(c, StringContext.treatEscapes)(arguments)

      final def fastraw_impl(c: Context)(
          arguments: c.Expr[Any]*
      ): c.Expr[Fastring] =
        newLocalFastringClass(c, identity)(arguments)

    }

    implicit final class FastringContext(val stringContext: StringContext)
        extends AnyVal {
      import FastringContext._

      final def fastraw(arguments: Any*) = macro fastraw_impl

      final def fast(arguments: Any*) = macro fast_impl

    }

    final object MkFastring {

      final def mkFastring_impl(c: Context): c.Expr[Fastring] = {
        import c.universe._
        val Select(mkFastringTree, _) = c.macroApplication
        val mkFastringExpr = c.Expr[MkFastring[_]](mkFastringTree)
        reify {
          // Workaround for https://issues.scala-lang.org/browse/SI-6711
          val m = mkFastringExpr.splice
          new _root_.com.dongxiguo.fastring.Fastring {
            @inline
            override final def foreach[U](
                visitor: _root_.scala.Predef.String => U
            ) {
              for (subCollection <- m.underlying) {
                _root_.com.dongxiguo.fastring
                  .Fastring(subCollection)
                  .foreach(visitor)
              }
            }
          }
        }
      }

      final def mkFastringWithSeperator_impl(
          c: Context
      )(seperator: c.Expr[String]): c.Expr[Fastring] = {
        import c.universe._
        val Apply(Select(mkFastringTree, _), _) = c.macroApplication
        val mkFastringExpr = c.Expr[MkFastring[_]](mkFastringTree)
        reify {
          // Workaround for https://issues.scala-lang.org/browse/SI-6711
          val s = seperator.splice
          val m = mkFastringExpr.splice
          new _root_.com.dongxiguo.fastring.Fastring {
            @inline
            override final def foreach[U](
                visitor: _root_.scala.Predef.String => U
            ) {
              var first = true
              for (subCollection <- m.underlying) {
                if (first) {
                  first = false
                } else {
                  visitor(s)
                }
                _root_.com.dongxiguo.fastring
                  .Fastring(subCollection)
                  .foreach(visitor)
              }
            }
          }
        }
      }
    }

    implicit final class MkFastring[A](val underlying: TraversableOnce[A])
        extends AnyVal {
      import MkFastring._
      final def mkFastring: Fastring = macro mkFastring_impl
      final def mkFastring(seperator: String): Fastring =
        macro mkFastringWithSeperator_impl
    }

    implicit final class ArrayMkFastring[A](val underlying: Array[A])
        extends AnyVal {
      import MkFastring._
      final def mkFastring: Fastring = macro mkFastring_impl
      final def mkFastring(seperator: String): Fastring =
        macro mkFastringWithSeperator_impl
    }

    implicit final class LongLeftPadOps(val underlying: Long) extends AnyVal {
      @inline
      final def leftPad(
          minWidth: Int,
          filledChar: Char = ' ',
          radix: Int = 10
      ) =
        new LeftPadLong(underlying, minWidth, filledChar, radix)
    }

    @deprecated(message = "Use `leftPad` instead", since = "0.3.0")
    implicit final class LongFilled(val underlying: Long) extends AnyVal {
      @deprecated(message = "Use `leftPad` instead", since = "0.3.0")
      @inline
      final def filled(minWidth: Int, filledChar: Char = ' ', radix: Int = 10) =
        new FilledLong(underlying, minWidth, filledChar, radix)
    }

    implicit final class IntLeftPadOps(val underlying: Int) extends AnyVal {
      @inline
      final def leftPad(
          minWidth: Int,
          filledChar: Char = ' ',
          radix: Int = 10
      ) =
        new LeftPadInt(underlying, minWidth, filledChar, radix)
    }

    @deprecated(message = "Use `leftPad` instead", since = "0.3.0")
    implicit final class IntFilled(val underlying: Int) extends AnyVal {

      @deprecated(message = "Use `leftPad` instead", since = "0.3.0")
      @inline
      final def filled(minWidth: Int, filledChar: Char = ' ', radix: Int = 10) =
        new FilledInt(underlying, minWidth, filledChar, radix)

    }

    import language.implicitConversions
    @inline
    implicit final def byteFilled(byte: Byte) = new IntFilled(byte)

    @inline
    implicit final def shortFilled(short: Short) = new IntFilled(short)

  }
}
