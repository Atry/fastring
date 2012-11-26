package com.dongxiguo.fastring

import scala.reflect.macros.Context
import language.experimental.macros
import scala.collection.TraversableLike
import scala.collection.mutable.LazyBuilder
import scala.collection.generic.CanBuildFrom
import scala.reflect.macros.Context

abstract class Fastring extends TraversableLike[String, Fastring] with Traversable[String] { self =>
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

  final override def toString = mkString

}

final object Fastring {

  final class FromAny(any: Any) extends Fastring {
    @inline
    override final def foreach[U](visitor: String => U) {
      visitor(any.toString)
    }
  }

  final class FromString(string: String) extends Fastring {

    @inline
    override final def foreach[U](visitor: String => U) {
      visitor(string)
    }
  }

  final class FilledLong(value: Long, minWidth: Int, filledChar: Char, radix: Int)
    extends Fastring {
    override final def foreach[U](visitor: String => U) {
      val unfilled = java.lang.Long.toString(value, radix)
      if (unfilled.length < minWidth) {
        if (value >= 0) {
          visitor(new String(Array.fill(minWidth - unfilled.length)(filledChar)))
          visitor(unfilled)
        } else {
          visitor("-")
          visitor(new String(Array.fill(minWidth - unfilled.length)(filledChar)))
          visitor(unfilled.substring(1))
        }
      } else {
        visitor(unfilled)
      }
    }
  }

  final class FilledInt(value: Int, minWidth: Int, filledChar: Char, radix: Int)
    extends Fastring {
    override final def foreach[U](visitor: String => U) {
      val unfilled = java.lang.Integer.toString(value, radix)
      if (unfilled.length < minWidth) {
        if (value >= 0) {
          visitor(new String(Array.fill(minWidth - unfilled.length)(filledChar)))
          visitor(unfilled)
        } else {
          visitor("-")
          visitor(new String(Array.fill(minWidth - unfilled.length)(filledChar)))
          visitor(unfilled.substring(1))
        }
      } else {
        visitor(unfilled)
      }
    }
  }

  @inline
  final def apply[A <: Fastring](fastring: A): A = fastring

  @inline
  final def apply(any: Any) = new FromAny(any)

  final def applyVarargs_impl(c: Context)(argument1: c.Expr[Any], argument2: c.Expr[Any], rest: c.Expr[Any]*): c.Expr[Fastring] = {
    val visitorExpr = c.Expr[String => _](c.universe.Ident("visitor"))
    val foreachBodyExpr = rest.foldLeft[c.Expr[Unit]](
      c.universe.reify {
        Fastring(argument1).foreach(visitorExpr.splice)
        Fastring(argument2).foreach(visitorExpr.splice)
      }) { (prefixExpr, argument) =>
        c.universe.reify {
          prefixExpr.splice
          Fastring(argument).foreach(visitorExpr.splice)
        }
      }
    c.universe.reify {
      new Fastring {
        @inline
        override final def foreach[U](visitor: String => U) {
          foreachBodyExpr.splice
        }
      }
    }
  }

  @inline
  final def apply(argument1: Any, argument2: Any, rest: Any*): Fastring = macro applyVarargs_impl

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
        override final def foreach[U](visitor: String => U) {
          for (part <- parts) {
            part.foreach(visitor)
          }
        }
      }
    }
  }

  final object Implicits {
    object FastringContext {
      final def fast_impl(c: Context)(arguments: c.Expr[Any]*): c.Expr[Fastring] = {

        val c.universe.Apply(c.universe.Select(fastringContextContextTree, _), _) =
          c.macroApplication
        val fastringContextExpr = c.Expr[FastringContext](fastringContextContextTree)
        val visitAllExpr =
          0.until(arguments.length).foldLeft[c.Expr[Unit]](c.Expr(c.universe.EmptyTree)) { (prefixExpr, i) =>
            val visitorExpr = c.Expr[String => _](c.universe.Ident("visitor"))
            val partsExpr = c.Expr[Seq[String]](c.universe.Ident("parts"))
            val iExpr = c.Expr[Int](c.universe.Literal(c.universe.Constant(i)))
            val argumentExpr = c.Expr[Any](c.universe.Ident("__arguments" + i))
            c.universe.reify {
              prefixExpr.splice
              visitorExpr.splice(partsExpr.splice(iExpr.splice))
              Fastring(argumentExpr.splice).foreach(visitorExpr.splice)
            }
          }
        // Workaround for https://issues.scala-lang.org/browse/SI-6711
        val valdefTrees =
          (for ((argumentExpr, i) <- arguments.iterator.zipWithIndex) yield {
            c.universe.ValDef(
              c.universe.Modifiers(),
              c.universe.newTermName("__arguments" + i),
              c.universe.TypeTree(),
              argumentExpr.tree)
          })
        val numArgumentsExpr =
          c.Expr[Int](c.universe.Literal(c.universe.Constant(arguments.length)))
        val newFastringExpr =
          c.universe.reify {
            new Fastring {
              @inline
              override final def foreach[U](visitor: String => U) {
                val parts = fastringContextExpr.splice.stringContext.parts
                assert(parts.length == numArgumentsExpr.splice + 1)
                visitAllExpr.splice
                visitor(parts(numArgumentsExpr.splice))
              }
            }
          }
        c.Expr(c.universe.Block(valdefTrees.toList, newFastringExpr.tree))
      }
    }

    implicit final class FastringContext(val stringContext: StringContext) extends AnyVal {
      import FastringContext._

      @inline
      final def fast() = Fastring(stringContext.parts(0))

      @inline
      final def fast(arguments: Any*) = macro fast_impl
    }

    final object MkFastring {

      final def mkFastring_impl(c: Context): c.Expr[Fastring] = {
        val c.universe.Select(mkFastringTree, _) =
          c.macroApplication
        val mkFastringExpr = c.Expr[MkFastring[_]](mkFastringTree)
        c.universe.reify {
          // Workaround for https://issues.scala-lang.org/browse/SI-6711
          val m = mkFastringExpr.splice
          new Fastring {
            @inline
            override final def foreach[U](visitor: String => U) {
              for (subCollection <- m.underlying) {
                Fastring(subCollection).foreach(visitor)
              }
            }
          }
        }
      }

      final def mkFastringWithSeperator_impl(c: Context)(seperator: c.Expr[String]): c.Expr[Fastring] = {
        val c.universe.Apply(c.universe.Select(mkFastringTree, _), _) =
          c.macroApplication
        val mkFastringExpr = c.Expr[MkFastring[_]](mkFastringTree)
        c.universe.reify {
          // Workaround for https://issues.scala-lang.org/browse/SI-6711
          val s = seperator.splice
          val m = mkFastringExpr.splice
          new Fastring {
            @inline
            override final def foreach[U](visitor: String => U) {
              var first = true
              for (subCollection <- m.underlying) {
                if (first) {
                  first = false
                } else {
                  visitor(s)
                }
                Fastring(subCollection).foreach(visitor)
              }
            }
          }
        }
      }
    }

    implicit final class MkFastring[A](val underlying: TraversableOnce[A]) extends AnyVal {
      import MkFastring._
      final def mkFastring: Fastring = macro mkFastring_impl
      final def mkFastring(seperator: String): Fastring = macro mkFastringWithSeperator_impl
    }

    implicit final class LongFilled(underlying: Long) {
      final def filled(minWidth: Int, filledChar: Char = ' ', radix: Int = 10) =
        new FilledLong(underlying, minWidth, filledChar, radix)
    }

    implicit final class IntFilled(underlying: Int) {

      final def filled(minWidth: Int, filledChar: Char = ' ', radix: Int = 10) =
        new FilledInt(underlying, minWidth, filledChar, radix)
    }

    import language.implicitConversions
    implicit final def byteFilled(byte: Byte) = new IntFilled(byte)
    implicit final def shortFilled(short: Short) = new IntFilled(short)

  }
}
