# Fastring

**Fastring** is a string formatting library for [Scala](http://www.scala-lang.org/).
`Fastring` is also designed to be a template engine,
and it is an excellent replacement of JSP, [Scalate](http://scalate.fusesource.org/) or [FreeMarker](http://freemarker.sourceforge.net/).

## It's simple to use

`Fastring` uses [string interpolation](http://docs.scala-lang.org/sips/pending/string-interpolation.html) syntax.
For example, if you are writing a CGI page:

    import com.dongxiguo.fastring.Fastring.Implicits._
    def printHtml(link: java.net.URL) {
      val fastHtml = fast"<html><body><a href='$link'>Click Me!</a></body></html>"
      print(fastHtml)
    }

## It's extremely fast

I made a [benchmark](https://github.com/Atry/fastring/blob/master/benchmark/src/main/scala/com/dongxiguo/fastring/benchmark/FastringBenchmark.scala).
I used 4 different ways to create a 545-length string.

1. Fastring (`fast"Concat with $something"` syntax);
2. String concatenation (`s"Concat with $something"` syntax);
3. Handwritten `StringBuilder` (`stringBuilder ++= "Build from " ++= something` syntax);
4. `java.util.Formatter` (`f"Format with $something"` syntax).

This is the result from my Intel i5-3450 computer:

<table>
<tr>
<th>
Fastring
</th>
<td>
<pre><code>import com.dongxiguo.fastring.Fastring.Implicits._
def fast(a: Int) =
  fast"head ${
    (for (j &lt;- 0 until 10 view) yield {
      fast"baz$j $a foo ${
        (for (i &lt;- 0 until 4 view) yield {
          fast"$a i=$i"
        }).mkFastring(",")
      } bar\n"
    }).mkFastring("&lt;hr/&gt;")
  } tail"

fast(0).toString</code></pre>
</td>
<td>
Took 669 nanoseconds to generate a 545-length string.<br/>(Simple and fast)
</td>
</tr>
<tr>
<th>
String concatenation
</th>
<td>
<pre><code>def s(a: Int) =
  s"head ${
    (for (j &lt;- 0 until 10 view) yield {
      s"baz$j $a foo ${
        (for (i &lt;- 0 until 4 view) yield {
          s"$a i=$i"
        }).mkString(",")
      } bar\n"
    }).mkString("&lt;hr/&gt;")
  } tail"

s(0)</code></pre>
</td>
<td>
Took 1738 nanoseconds to generate a 545-length string.<br/>(Simple but slow)
</td>
</tr>
<tr>
<th>
Handwritten <code>StringBuilder</code>
</th>
<td>
<pre><code>def sb(sb: StringBuilder, a: Int) {
  sb ++= "head "
  var first = true
  for (j &lt;- 0 until 10 view) {
    if (first) {
      first = false
    } else {
      sb ++= ""&lt;hr/&gt;""
    }
    sb ++=
      "baz" ++= j.toString ++=
      " " ++= a.toString ++= " foo ";
    {
      var first = true
      for (i &lt;- 0 until 4 view) {
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

val s = new StringBuilder
sb(s, 0)
s.toString</code></pre>
</td>
<td>
Took 537 nanoseconds to generate a 545-length string.<br/>(Fast but too trivial)
</td>
</tr>
<tr>
<th>
<code>java.util.Formatter</code>
</th>
<td>
<pre><code>def f(a: Int) =
    f"head ${
      (for (j &lt;- 0 until 10 view) yield {
        f"baz$j $a foo ${
          (for (i &lt;- 0 until 4 view) yield {
            f"$a i=$i"
          }).mkString(",")
        } bar\n"
      }).mkString("&lt;hr/&gt;")
    } tail"

f(0)</code></pre>
</td>
<td>
Took 7436 nanoseconds to generate a 545-length string.<br/>(Simple but extremely slow)
</td>
</tr>
</table>

`Fastring` is so fast because it is **lazily** evaluated.
It avoids coping content for nested String Interpolation.
Thus, `Fastring` is very suitable to generate complex text content(e.g. HTML, JSON).

For example, in the previous benchmark for `Fastring`, the most of time was spent on invoking `toString`.
You can avoid these overhead if you do not need a whole string. For example:

    // Faster than: print(fast"My lazy string from $something")
    fast"My lazy string from $something".foreach(print)

You can invoke `foreach` because `Fastring` is just a `Traversable[String]`.

## Utilities

There is a `mkFastring` method for `Seq`:

    // Enable mkFastring method
    import com.dongxiguo.fastring.Fastring.Implicits._
    
    // Got Fastring("Seq.mkFastring: Hello, world")
    fast"Seq.mkFastring: ${Seq("Hello", "world").mkFastring(", ")}"
    
    // Also works, but slower:
    // Got Fastring("Seq.mkString: Hello, world")
    fast"Seq.mkString: ${Seq("Hello", "world").mkString(", ")}"

And a `filled` method for `Byte`, `Short`, `Int` and `Long`:

    // Enable filled method
    import com.dongxiguo.fastring.Fastring.Implicits._
    
    // Got Fastring("Int.filled:   123")
    fast"Int.filled: ${123.filled(5)}"
    
    // Got Fastring("Int.filled: 00123")
    fast"Int.filled: ${123.filled(5, '0')}"

## Installation

Put these lines in your `build.sbt` if you use Sbt(http://www.scala-sbt.org/) `0.12.x`:

    libraryDependencies += "com.dongxiguo" %% "fastring" % "0.2.1"
    
    scalaVersion := "2.10.1"

See http://mvnrepository.com/artifact/com.dongxiguo/fastring_2.10/0.2.1 if you use [Maven](http://maven.apache.org/)
or other build systems.

Note that `Fastring` requires [Scala](http://www.scala-lang.org/) `2.10.x`.
