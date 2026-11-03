package datadog.instrument.classmatch;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/**
 * Gated DD_TRACE matching: models an agent where a type/classloader gate has already passed, so
 * only one instrumentation's selectors (its "group", ~1.5 selectors on average) are evaluated
 * against a class. This removes the global name index's breadth advantage and compares matcher
 * quality alone.
 *
 * <p>Pattern variants: a small per-group {@link PatternIndex}, and a plain linear loop over the
 * group's compiled {@link MemberPattern}s with no index (small sets can make index overhead
 * dominate). Groups containing a predicate fallback use the fallback in both pattern variants.
 *
 * <p>Pairings: {@code RANDOM} (class, group) pairs are miss-dominated, like loose gates; {@code
 * HITS} only pairs where the group matches the class, a proxy for "the gate passed on the right
 * class".
 */
@State(Scope.Thread)
@BenchmarkMode(AverageTime)
@OutputTimeUnit(NANOSECONDS)
@Fork(
    value = 3,
    jvmArgsAppend = {"-Xms512m", "-Xmx512m"})
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
public class PatternGatedBenchmark {
  private static final int PAIRS = 1 << 16;

  @Param({"RANDOM", "HITS"})
  public String pairing;

  private ClassOutline[] outlines;
  private ClassMatcher[][] groupMatchers; // class-match predicates per group
  private PatternIndex[] groupIndexes; // per-group pattern index
  private MemberPattern[][] groupPatterns; // per-group compiled patterns (null if fallback)
  private int[][] groupPatternSelector; // pattern -> selector offset within group
  private ClassMatcher[][] groupFallbacks; // per-group fallbacks, aligned with selectors
  private int[] pairClass;
  private int[] pairGroup;
  private int cursor;

  @Setup
  public void setup() {
    ClassMatcher[] branches = DDTraceClassMatcherGraphs.branches();
    String[][] patterns = DDTracePatterns.patterns();
    ClassMatcher[] fallbacks = DDTracePatterns.fallbacks();
    int[] groups = DDTracePatterns.groups();
    new PatternIndex(patterns, fallbacks, DDTracePatterns.annotations()); // register annotations
    int groupCount = 0;
    for (int g : groups) {
      groupCount = Math.max(groupCount, g + 1);
    }
    List<List<Integer>> members = new ArrayList<>();
    for (int g = 0; g < groupCount; g++) {
      members.add(new ArrayList<>());
    }
    for (int s = 0; s < groups.length; s++) {
      members.get(groups[s]).add(s);
    }
    groupMatchers = new ClassMatcher[groupCount][];
    groupIndexes = new PatternIndex[groupCount];
    groupPatterns = new MemberPattern[groupCount][];
    groupPatternSelector = new int[groupCount][];
    groupFallbacks = new ClassMatcher[groupCount][];
    for (int g = 0; g < groupCount; g++) {
      List<Integer> sel = members.get(g);
      int n = sel.size();
      if (n > 64) {
        throw new IllegalStateException("Group " + g + " has " + n + " selectors; max 64");
      }
      groupMatchers[g] = new ClassMatcher[n];
      String[][] groupPatternStrings = new String[n][];
      groupFallbacks[g] = new ClassMatcher[n];
      List<MemberPattern> compiled = new ArrayList<>();
      List<Integer> owner = new ArrayList<>();
      for (int i = 0; i < n; i++) {
        int s = sel.get(i);
        groupMatchers[g][i] = branches[s];
        groupPatternStrings[i] = patterns[s];
        groupFallbacks[g][i] = fallbacks[s];
        if (patterns[s] != null) {
          for (String p : patterns[s]) {
            compiled.add(MemberPattern.compile(p));
            owner.add(i);
          }
        }
      }
      groupIndexes[g] = new PatternIndex(groupPatternStrings, groupFallbacks[g]);
      groupPatterns[g] = compiled.toArray(new MemberPattern[0]);
      groupPatternSelector[g] = owner.stream().mapToInt(Integer::intValue).toArray();
    }

    byte[][] bytecode = PatternCorpus.bytecode();
    outlines = new ClassOutline[bytecode.length];
    for (int i = 0; i < bytecode.length; i++) {
      outlines[i] = ClassFile.outline(bytecode[i]);
    }

    // choose (class, group) pairs and check all three implementations agree on each
    SplittableRandom random = new SplittableRandom(42);
    pairClass = new int[PAIRS];
    pairGroup = new int[PAIRS];
    boolean hitsOnly = "HITS".equals(pairing);
    int filled = 0;
    int attempts = 0;
    int hits = 0;
    while (filled < PAIRS) {
      if (++attempts > 200_000_000) {
        throw new IllegalStateException("Could not find enough pairs: " + filled);
      }
      int c = random.nextInt(outlines.length);
      int g = random.nextInt(groupCount);
      int expected = countClassMatcher(g, outlines[c]);
      if (hitsOnly && expected == 0) {
        continue;
      }
      if (groupIndexes[g].countMatches(outlines[c]) != expected
          || countLinear(g, outlines[c]) != expected) {
        throw new IllegalStateException("Disagreement on " + outlines[c].className + " group " + g);
      }
      hits += expected > 0 ? 1 : 0;
      pairClass[filled] = c;
      pairGroup[filled] = g;
      filled++;
    }
    System.out.printf(
        "%nGated %s: %d groups; %d pairs, %d with hits; agreement OK%n",
        pairing, groupCount, PAIRS, hits);
  }

  private int countClassMatcher(int g, ClassOutline outline) {
    int hits = 0;
    for (ClassMatcher m : groupMatchers[g]) {
      if (m.test(outline)) {
        hits++;
      }
    }
    return hits;
  }

  /** Linear scan: each member against each of the group's patterns; no index. */
  private int countLinear(int g, ClassOutline outline) {
    MemberPattern[] ps = groupPatterns[g];
    int[] owner = groupPatternSelector[g];
    ClassMatcher[] fbs = groupFallbacks[g];
    long matched = 0; // groups are small: bit per selector offset (max 64 per group)
    for (int i = 0; i < ps.length; i++) {
      MemberPattern p = ps[i];
      long bit = 1L << owner[i];
      if ((matched & bit) != 0) {
        continue;
      }
      if (p.field) {
        for (FieldOutline f : outline.fields) {
          if (p.nameMatches(f.fieldName) && p.matches(f.access, f.descriptor)) {
            matched |= bit;
            break;
          }
        }
      } else {
        for (MethodOutline m : outline.methods) {
          if (p.nameMatches(m.methodName) && p.matches(m)) {
            matched |= bit;
            break;
          }
        }
      }
    }
    for (int i = 0; i < fbs.length; i++) {
      if (fbs[i] != null && fbs[i].test(outline)) {
        matched |= 1L << i;
      }
    }
    return Long.bitCount(matched);
  }

  private int next() {
    int i = cursor;
    cursor = (i + 1) & (PAIRS - 1);
    return i;
  }

  @Benchmark
  public int gatedClassMatcher() {
    int i = next();
    return countClassMatcher(pairGroup[i], outlines[pairClass[i]]);
  }

  @Benchmark
  public int gatedPatternIndex() {
    int i = next();
    return groupIndexes[pairGroup[i]].countMatches(outlines[pairClass[i]]);
  }

  @Benchmark
  public int gatedPatternLinear() {
    int i = next();
    return countLinear(pairGroup[i], outlines[pairClass[i]]);
  }
}
