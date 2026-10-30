package datadog.instrument.classmatch;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

import datadog.instrument.testing.SampleClasses;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.pool.TypePool;
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
 * Warmed execution of equivalent instrumentation-style matcher graphs over unloaded classes from
 * jars commonly found in Spring applications (Spring Web, MVC, WebFlux, core, beans, context, AOP,
 * Jackson, Reactor, Tomcat, and Apache HttpClient) plus generated application controller fixtures.
 * Each operation applies a prebuilt graph to one class, cycling through the corpus. Metadata
 * parsing and graph construction happen during setup, which checks agreement between the APIs for
 * every graph, branch, and class and requires both hits and misses per branch. Controller fixtures
 * also assert expected positive and negative results.
 *
 * <p>The COMBINED API-coverage workload models request handlers, HTTP clients, stream accessors,
 * header fields paired with getters, and annotated endpoints, using nested conjunctions and
 * disjunctions. Matching uses declared members and direct annotations, without hierarchy traversal
 * or meta-annotation lookup. Normal short-circuit behavior is preserved, so execution cost depends
 * on the corpus hit distribution.
 *
 * <p>CLASS_SELECTION adds common declared-member and single-annotation patterns from real
 * instrumentation class-selection roots. TYPE_NAMES validates the CharSequence type-name API on
 * class names through a small adapter, without traversing a hierarchy. STRUCTURAL and
 * STRUCTURAL_SIGNATURES compare rare fingerprints expressed with indexed checks or complete
 * parameter signatures. SelectionFixtures supplies hits and deliberate near misses, including wrong
 * annotation, field type, parameter type, arity, and missing-member examples.
 *
 * <p>From the repository root, build with {@code ./gradlew :class-match:jmhJar
 * :class-match:sampleBytecode}, then run:
 *
 * <pre>{@code
 * cd class-match
 * mkdir -p build/results/jmh
 * "$JAVA_17_HOME/bin/java" -jar build/libs/class-match-*-jmh.jar \
 *   MatchingApiBenchmark -foe true -prof gc \
 *   -rf json -rff build/results/jmh/matching.json
 * }</pre>
 *
 * <p>This uses two forks, three one-second warmup iterations and five one-second measurement
 * iterations. JMH reports average time in ns/op and allocation in gc.alloc.rate.norm (bytes per
 * class matched). Results return the match boolean to JMH. With {@code profile=SHARED}, scores
 * report average time and allocation across the entire graph mix. The selected workload changes its
 * weighting in that mix; scores do not measure the selected graph alone under shared profiling.
 *
 * <p>To select the combined graph, add {@code -p workload=COMBINED}. For a quick validation run,
 * add {@code -wi 1 -i 1 -w 200ms -r 200ms -f 1 -t 1}; use the normal settings for performance
 * comparisons.
 *
 * <p>Replace JAVA_17_HOME with the desired JDK home. See {@link ColdMatcherConstructionBenchmark}
 * for first-use graph construction with the same factories.
 */
@State(Scope.Thread)
@BenchmarkMode(AverageTime)
@OutputTimeUnit(NANOSECONDS)
@Fork(
    value = 2,
    jvmArgsAppend = {"-Xms512m", "-Xmx512m"})
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class MatchingApiBenchmark {

  @Param({
    "REAL_WORLD",
    "CLASS_SELECTION",
    "TYPE_NAMES",
    "STRUCTURAL",
    "STRUCTURAL_SIGNATURES",
    "COMBINED"
  })
  public MatcherWorkload workload;

  /**
   * ISOLATED uses only the selected graph. SHARED cycles through the selected graph followed by
   * every workload's graph, running one complete corpus pass per graph. Both APIs keep this mix
   * active throughout warmup and measurement. The selected graph is evaluated twice per cycle and
   * each other graph once. Scores average across this mix; selecting a workload changes its
   * weighting rather than restricting measurement to that graph.
   */
  @Param({"ISOLATED", "SHARED"})
  public String profile;

  private ClassOutline[] outlines;
  private TypeDescription[] descriptions;
  private ClassMatcher[] classMatchers;
  private ElementMatcher.Junction<TypeDescription>[] byteBuddyMatchers;
  private int cursor;
  private int matcherIndex;

  @Setup
  @SuppressWarnings("unchecked")
  public void setup() {
    Map<String, byte[]> corpus = new LinkedHashMap<>();
    for (String jar :
        new String[] {
          "spring-web.jar",
          "spring-webmvc.jar",
          "spring-webflux.jar",
          "spring-core.jar",
          "spring-beans.jar",
          "spring-context.jar",
          "spring-aop.jar",
          "jackson-core.jar",
          "jackson-databind.jar",
          "reactor-core.jar",
          "tomcat-embed-core.jar",
          "httpclient5.jar",
          "httpcore5.jar"
        }) {
      for (byte[] bytecode : SampleClasses.loadBytecode(jar)) {
        String name = ClassFile.header(bytecode).className.replace('/', '.');
        // Multi-release entries describe alternate versions of the same class. Use one per name.
        if (!name.equals("module-info")) {
          corpus.putIfAbsent(name, bytecode);
        }
      }
    }
    if (corpus.isEmpty()) {
      throw new IllegalStateException("Empty Spring application corpus");
    }
    Map<String, Boolean> fixtureExpectations = ControllerFixtures.addTo(corpus);
    Map<String, boolean[]> selectionExpectations = SelectionFixtures.addTo(corpus);
    String[] names = corpus.keySet().toArray(new String[0]);
    byte[][] bytecodes = corpus.values().toArray(new byte[0][]);
    ClassFileLocator locator =
        new ClassFileLocator.Compound(
            new ClassFileLocator.Simple(corpus), ClassFileLocator.ForClassLoader.ofSystemLoader());
    // Lazy resolution allows descriptor/name matching without loading optional servlet/reactor
    // APIs.
    TypePool cachedPool =
        new TypePool.Default.WithLazyResolution(
            new TypePool.CacheProvider.Simple(), locator, TypePool.Default.ReaderMode.FAST);
    MatcherWorkload[] workloads;
    if ("SHARED".equals(profile)) {
      MatcherWorkload[] all = MatcherWorkload.values();
      workloads = new MatcherWorkload[all.length + 1];
      workloads[0] = workload;
      System.arraycopy(all, 0, workloads, 1, all.length);
    } else {
      workloads = new MatcherWorkload[] {workload};
    }
    classMatchers = new ClassMatcher[workloads.length];
    byteBuddyMatchers = new ElementMatcher.Junction[workloads.length];
    ClassMatcher[][] branches = new ClassMatcher[workloads.length][];
    ElementMatcher.Junction<TypeDescription>[][] byteBuddyBranches =
        new ElementMatcher.Junction[workloads.length][];
    int[][] branchHits = new int[workloads.length][];
    // Build before parsing, so outlines include every graph's annotations of interest.
    for (int i = 0; i < workloads.length; i++) {
      classMatchers[i] = ClassMatcherGraphs.create(workloads[i]);
      byteBuddyMatchers[i] = ByteBuddyGraphs.create(workloads[i]);
      branches[i] = ClassMatcherGraphs.branches(workloads[i]);
      byteBuddyBranches[i] = ByteBuddyGraphs.branches(workloads[i]);
      branchHits[i] = new int[branches[i].length];
    }
    outlines = new ClassOutline[names.length];
    descriptions = new TypeDescription[names.length];
    int[] hits = new int[workloads.length];
    for (int i = 0; i < names.length; i++) {
      outlines[i] = ClassFile.outline(bytecodes[i]);
      descriptions[i] = cachedPool.describe(names[i]).resolve();
      for (int j = 0; j < workloads.length; j++) {
        boolean expected = classMatchers[j].test(outlines[i]);
        if (byteBuddyMatchers[j].matches(descriptions[i]) != expected) {
          throw new IllegalStateException(
              "Matcher disagreement: " + workloads[j] + " / " + names[i]);
        }
        Boolean fixtureExpected = fixtureExpectations.get(names[i]);
        if (fixtureExpected != null
            && expected != (workloads[j] == MatcherWorkload.COMBINED && fixtureExpected)) {
          throw new IllegalStateException(
              "Unexpected fixture result: " + workloads[j] + " / " + names[i]);
        }
        boolean[] selectionExpected = selectionExpectations.get(names[i]);
        if (selectionExpected != null
            && expected != SelectionFixtures.expected(selectionExpected, workloads[j])) {
          throw new IllegalStateException(
              "Unexpected selection fixture: " + workloads[j] + " / " + names[i]);
        }
        hits[j] += expected ? 1 : 0;
        for (int k = 0; k < branches[j].length; k++) {
          boolean branchMatch = branches[j][k].test(outlines[i]);
          if (byteBuddyBranches[j][k].matches(descriptions[i]) != branchMatch) {
            throw new IllegalStateException(
                "Branch disagreement: " + workloads[j] + "[" + k + "] / " + names[i]);
          }
          branchHits[j][k] += branchMatch ? 1 : 0;
        }
      }
    }
    // Both structural forms must agree on the entire corpus, beyond the explicit fixtures.
    ClassMatcher indexed = ClassMatcherGraphs.create(MatcherWorkload.STRUCTURAL);
    ClassMatcher signatures = ClassMatcherGraphs.create(MatcherWorkload.STRUCTURAL_SIGNATURES);
    for (int i = 0; i < outlines.length; i++) {
      if (indexed.test(outlines[i]) != signatures.test(outlines[i])) {
        throw new IllegalStateException("Structural forms disagree: " + names[i]);
      }
    }
    for (int i = 0; i < workloads.length; i++) {
      if (hits[i] == 0 || hits[i] == names.length) {
        throw new IllegalStateException("Workload needs both hits and misses: " + workloads[i]);
      }
      for (int k = 0; k < branchHits[i].length; k++) {
        if (branchHits[i][k] == 0 || branchHits[i][k] == names.length) {
          throw new IllegalStateException(
              "Branch needs both hits and misses: " + workloads[i] + "[" + k + "]");
        }
      }
    }
    System.out.printf(
        "%s / %s: %d classes, %d hits, %d misses for selected graph; %d graphs%n",
        profile, workload, names.length, hits[0], names.length - hits[0], workloads.length);
    System.out.println("Branch hits (in graph factory order): " + Arrays.toString(branchHits[0]));
  }

  @Benchmark
  public boolean executeClassMatcher() {
    ClassMatcher matcher = classMatchers[matcherIndex];
    return matcher.test(outlines[nextClass()]);
  }

  @Benchmark
  public boolean executeByteBuddyMatcher() {
    ElementMatcher.Junction<TypeDescription> matcher = byteBuddyMatchers[matcherIndex];
    return matcher.matches(descriptions[nextClass()]);
  }

  private int nextClass() {
    int index = cursor;
    if (index + 1 == outlines.length) {
      cursor = 0;
      matcherIndex = matcherIndex + 1 == classMatchers.length ? 0 : matcherIndex + 1;
    } else {
      cursor = index + 1;
    }
    return index;
  }
}
