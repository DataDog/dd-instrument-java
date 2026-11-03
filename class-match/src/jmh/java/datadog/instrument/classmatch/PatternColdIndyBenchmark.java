package datadog.instrument.classmatch;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.openjdk.jmh.annotations.Mode.SingleShotTime;

import java.util.function.Supplier;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/**
 * Same construction cases as {@link PatternColdBenchmark}, but with the JVM's lambda/indy
 * infrastructure already initialized (as it usually is in an application that has run a lambda), to
 * separate that one-time JVM cost from matcher construction itself.
 */
@State(Scope.Thread)
@BenchmarkMode(SingleShotTime)
@OutputTimeUnit(MILLISECONDS)
@Fork(
    value = 30,
    jvmArgsAppend = {"-Xms512m", "-Xmx512m"})
@Warmup(iterations = 0)
@Measurement(iterations = 1, batchSize = 1)
public class PatternColdIndyBenchmark {
  @Setup
  public void setup() {
    // initialize lambda metafactory / method-handle infrastructure with an unrelated lambda
    String captured = "x";
    Supplier<String> s = () -> captured + System.nanoTime();
    if (s.get().isEmpty()) {
      throw new IllegalStateException("unreachable: keeps the lambda result live");
    }
  }

  @Benchmark
  public Object constructClassMatcher() {
    return DDTraceClassMatcherGraphs.branches();
  }

  @Benchmark
  public Object constructPatterns() {
    return new PatternIndex(
        DDTracePatterns.patterns(), DDTracePatterns.fallbacks(), DDTracePatterns.annotations());
  }

  @Benchmark
  public Object constructFallbacksOnly() {
    return DDTracePatterns.fallbacks();
  }

  @Benchmark
  public Object constructPatternLiteralsOnly() {
    return DDTracePatterns.patterns();
  }
}
