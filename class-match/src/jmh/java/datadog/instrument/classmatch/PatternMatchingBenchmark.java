package datadog.instrument.classmatch;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

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
 * Warmed DD_TRACE matching: class-match predicate graphs vs lazily-compiled member patterns with a
 * name index ({@link PatternIndex}), over the same corpus as {@link MatchingApiBenchmark}.
 *
 * <p>Sweep reports how many of the 1,191 selectors match a class (what an agent needs to pick
 * instrumentations); union stops at the first match. The pattern side includes its predicate
 * fallbacks for the selectors patterns can't express.
 *
 * <p>The {@code parse*} variants re-parse the class-file on every operation, so per-member strings
 * are fresh (no cached {@link String#hashCode()} from earlier iterations), as in a real agent.
 */
@State(Scope.Thread)
@BenchmarkMode(AverageTime)
@OutputTimeUnit(NANOSECONDS)
@Fork(
    value = 2,
    jvmArgsAppend = {"-Xms512m", "-Xmx512m"})
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class PatternMatchingBenchmark {
  private byte[][] bytecode;
  private ClassOutline[] outlines;
  private ClassMatcher[] branches;
  private ClassMatcher union;
  private PatternIndex index;
  private int cursor;

  @Setup
  public void setup() {
    // build before parsing, so outlines include every annotation of interest
    branches = DDTraceClassMatcherGraphs.branches();
    union = DDTraceClassMatcherGraphs.create();
    index =
        new PatternIndex(
            DDTracePatterns.patterns(), DDTracePatterns.fallbacks(), DDTracePatterns.annotations());
    if (index.selectorCount() != branches.length) {
      throw new IllegalStateException(
          "Selector count mismatch: " + index.selectorCount() + " vs " + branches.length);
    }
    bytecode = PatternCorpus.bytecode();
    outlines = new ClassOutline[bytecode.length];
    String[][] patterns = DDTracePatterns.patterns();
    boolean[] matched = new boolean[branches.length];
    long branchHits = 0;
    int unionHits = 0;
    for (int i = 0; i < bytecode.length; i++) {
      ClassOutline outline = ClassFile.outline(bytecode[i]);
      outlines[i] = outline;
      index.matches(outline, matched);
      for (int k = 0; k < branches.length; k++) {
        boolean expected = branches[k].test(outline);
        if (matched[k] != expected) {
          throw new IllegalStateException(
              "Disagreement on "
                  + outline.className
                  + " selector "
                  + k
                  + " "
                  + (patterns[k] == null ? "(fallback)" : String.join(" | ", patterns[k]))
                  + ": classmatch="
                  + expected);
        }
        branchHits += expected ? 1 : 0;
      }
      boolean expectedUnion = union.test(outline);
      if (index.matchesAny(outline) != expectedUnion) {
        throw new IllegalStateException("Union disagreement on " + outline.className);
      }
      unionHits += expectedUnion ? 1 : 0;
    }
    int totalPatterns = 0;
    for (String[] p : patterns) {
      totalPatterns += p == null ? 0 : p.length;
    }
    System.out.printf(
        "%nAgreement OK: %d classes x %d selectors; %d branch hits; %d union hits; "
            + "%d of %d patterns compiled after a full corpus pass%n",
        bytecode.length,
        branches.length,
        branchHits,
        unionHits,
        index.compiledCount(),
        totalPatterns);
  }

  private int next() {
    int i = cursor;
    cursor = i + 1 == outlines.length ? 0 : i + 1;
    return i;
  }

  @Benchmark
  public int sweepClassMatcher() {
    ClassOutline outline = outlines[next()];
    int hits = 0;
    for (ClassMatcher branch : branches) {
      if (branch.test(outline)) {
        hits++;
      }
    }
    return hits;
  }

  @Benchmark
  public int sweepPatterns() {
    return index.countMatches(outlines[next()]);
  }

  @Benchmark
  public boolean unionClassMatcher() {
    return union.test(outlines[next()]);
  }

  @Benchmark
  public boolean unionPatterns() {
    return index.matchesAny(outlines[next()]);
  }

  @Benchmark
  public int parseOnly() {
    return ClassFile.outline(bytecode[next()]).methods.length;
  }

  @Benchmark
  public int parseAndSweepClassMatcher() {
    ClassOutline outline = ClassFile.outline(bytecode[next()]);
    int hits = 0;
    for (ClassMatcher branch : branches) {
      if (branch.test(outline)) {
        hits++;
      }
    }
    return hits;
  }

  @Benchmark
  public int parseAndSweepPatterns() {
    return index.countMatches(ClassFile.outline(bytecode[next()]));
  }
}
