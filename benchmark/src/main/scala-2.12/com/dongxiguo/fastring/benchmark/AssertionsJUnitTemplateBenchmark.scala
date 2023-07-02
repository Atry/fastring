import com.dongxiguo.fastring.Fastring.Implicits._
import io.gatling.commons.stats.assertion._
import io.gatling.commons.util.StringHelper.Eol
import io.gatling.core.stats.writer.RunMessage
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

package com.dongxiguo.fastring.benchmark {

  /** @author
    *   杨博 (Yang Bo)
    */
  object AssertionsJUnitTemplateBenchmark {

    @volatile
    var runMessage: RunMessage = RunMessage(
      simulationClassName = "MySimulationClassName",
      userDefinedSimulationId = Some("myUserDefinedSimulationId"),
      defaultSimulationId = "defaultSimulationId",
      start = 42L,
      runDescription = """My description:
                        blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah
                        blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah
                     """
    )
    @volatile
    var assertionResults: List[AssertionResult] = List(
      AssertionResult(
        assertion = Assertion(
          path = ForAll,
          target = CountTarget(FailedRequests),
          condition = Gt(1000.0)
        ),
        result = true,
        message =
          "my assertion message blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah",
        None
      ),
      AssertionResult(
        assertion = Assertion(
          path = Details(List("foo", "bar", "baz")),
          target = CountTarget(AllRequests),
          condition = Gte(9999.99)
        ),
        result = true,
        message =
          "my assertion message blah blah blah blah blah blah blah blah blah blah blah blah blah blah",
        Some(12.3)
      ),
      AssertionResult(
        assertion = Assertion(
          path = Global,
          target = CountTarget(SuccessfulRequests),
          condition = Gte(9999.99)
        ),
        result = false,
        message =
          "my assertion message blah blah blah blah blah blah blah blah blah blah blah blah",
        None
      )
    )

    /** @note
      *   This benchmark is a modified version of
      *   [[https://github.com/gatling/gatling/blob/62340816e95d212a4cee07b296a5c8dee73eaf59/gatling-charts/src/main/scala/io/gatling/charts/template/AssertionsJUnitTemplate.scala]]
      */
    @State(Scope.Benchmark)
    class FastringAssertionsJUnitTemplate {

      private[this] def printMessage(
          assertionResult: AssertionResult
      ): Fastring =
        if (assertionResult.result)
          fastraw"""<system-out>${assertionResult.message}</system-out>"""
        else
          fastraw"""<failure type="${assertionResult.assertion.path.printable}">Actual value: ${assertionResult.actualValue
              .getOrElse(-1)}</failure>"""

      private[this] def print(assertionResult: AssertionResult): Fastring =
        fastraw"""<testcase name="${assertionResult.message}" status="${assertionResult.result}" time="0">
  ${printMessage(assertionResult)}
</testcase>"""

      def getOutput: Fastring =
        fastraw"""<testsuite name="${runMessage.simulationClassName}" tests="${assertionResults.size}" errors="0" failures="${assertionResults
            .count(_.result == false)}" time="0">
${assertionResults.map(print).mkFastring(Eol)}
</testsuite>"""

      @Benchmark
      def benchmark() = {
        getOutput
      }

      @Benchmark
      def benchmarkToString() = {
        getOutput.toString
      }

    }

  }

}

package com.sizmek.fsi.workaround {

  @State(Scope.Benchmark)
  class SizmekFastStringInterpolatorAssertionsJUnitTemplate {
    import com.sizmek.fsi._
    import com.dongxiguo.fastring.benchmark.AssertionsJUnitTemplateBenchmark._

    private[this] def printMessage(assertionResult: AssertionResult): String =
      if (assertionResult.result)
        fraw"""<system-out>${assertionResult.message}</system-out>"""
      else
        fraw"""<failure type="${assertionResult.assertion.path.printable}">Actual value: ${assertionResult.actualValue
            .getOrElse(-1)}</failure>"""

    private[this] def print(assertionResult: AssertionResult): String =
      fraw"""<testcase name="${assertionResult.message}" status="${assertionResult.result}" time="0">
  ${printMessage(assertionResult)}
</testcase>"""

    def getOutput: String = {
      fraw"""<testsuite name="${runMessage.simulationClassName}" tests="${assertionResults.size}" errors="0" failures="${assertionResults
          .count(_.result == false)}" time="0">
${assertionResults.map(print).mkString(Eol)}
</testsuite>"""
    }

    @Benchmark
    def benchmark() = {
      getOutput
    }
  }
}
