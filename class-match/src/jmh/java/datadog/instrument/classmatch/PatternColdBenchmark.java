package datadog.instrument.classmatch;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.openjdk.jmh.annotations.Mode.SingleShotTime;

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
 * Cold DD_TRACE costs in a fresh JVM: construction alone, and first use (construction plus one pass
 * over the corpus, parsing each class then finding its matching selectors). Laziness moves pattern
 * compilation from construction into first use, so first use is the fair comparison.
 */
@State(Scope.Thread)
@BenchmarkMode(SingleShotTime)
@OutputTimeUnit(MILLISECONDS)
@Fork(
    value = 10,
    jvmArgsAppend = {"-Xms512m", "-Xmx512m"})
@Warmup(iterations = 0)
@Measurement(iterations = 1, batchSize = 1)
public class PatternColdBenchmark {
  private byte[][] bytecode;

  @Setup
  public void setup() {
    // raw bytes only: parsing happens inside the measured first-use operations
    bytecode = PatternCorpus.bytecode();
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

  /** Decomposition of constructPatterns: the pattern literals alone. */
  @Benchmark
  public Object constructPatternLiteralsOnly() {
    return DDTracePatterns.patterns();
  }

  /** Decomposition of constructPatterns: the 39 predicate fallbacks alone. */
  @Benchmark
  public Object constructFallbacksOnly() {
    return DDTracePatterns.fallbacks();
  }

  @Benchmark
  public long firstUseClassMatcher() {
    ClassMatcher[] branches = DDTraceClassMatcherGraphs.branches();
    long hits = 0;
    for (byte[] b : bytecode) {
      ClassOutline outline = ClassFile.outline(b);
      for (ClassMatcher branch : branches) {
        if (branch.test(outline)) {
          hits++;
        }
      }
    }
    return hits;
  }

  @Benchmark
  public long firstUsePatterns() {
    PatternIndex index =
        new PatternIndex(
            DDTracePatterns.patterns(), DDTracePatterns.fallbacks(), DDTracePatterns.annotations());
    long hits = 0;
    for (byte[] b : bytecode) {
      hits += index.countMatches(ClassFile.outline(b));
    }
    return hits;
  }

  /** Flat matchers built through the fluent API (Flat entry points), slots merged on and(). */
  @Benchmark
  public Object constructFlat() {
    return DDTraceFlatGraphs.branches();
  }

  @Benchmark
  public long firstUseFlat() {
    ClassMatcher[] branches = DDTraceFlatGraphs.branches();
    long hits = 0;
    for (byte[] b : bytecode) {
      ClassOutline outline = ClassFile.outline(b);
      for (ClassMatcher branch : branches) {
        if (branch.test(outline)) {
          hits++;
        }
      }
    }
    return hits;
  }

  /** Build-step variant: patterns precompiled into generated code, no parsing at runtime. */
  @Benchmark
  public Object constructPrebuilt() {
    return new PatternIndex(
        DDTracePatterns.patterns(),
        DDTracePatterns.fallbacks(),
        DDTracePrecompiled.compiled(),
        DDTracePatterns.annotations());
  }

  @Benchmark
  public long firstUsePrebuilt() {
    PatternIndex index =
        new PatternIndex(
            DDTracePatterns.patterns(),
            DDTracePatterns.fallbacks(),
            DDTracePrecompiled.compiled(),
            DDTracePatterns.annotations());
    long hits = 0;
    for (byte[] b : bytecode) {
      hits += index.countMatches(ClassFile.outline(b));
    }
    return hits;
  }

  /** Parsing alone, to separate the shared parse cost from matching in first use. */
  @Benchmark
  public long firstUseParseOnly() {
    long methods = 0;
    for (byte[] b : bytecode) {
      methods += ClassFile.outline(b).methods.length;
    }
    return methods;
  }
}
