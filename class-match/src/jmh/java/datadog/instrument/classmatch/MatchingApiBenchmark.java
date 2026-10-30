package datadog.instrument.classmatch;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

import datadog.instrument.testing.SampleClasses;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.bytebuddy.description.method.MethodDescription;
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
 * Each class operation applies a prebuilt graph or its branches to one class, cycling through the
 * corpus. Metadata parsing and graph construction happen during setup, which checks agreement
 * between the APIs for every graph, branch, and class and requires both hits and misses per branch
 * for the small workloads. DD_TRACE reports positive coverage separately; many selectors have only
 * misses. Controller fixtures also assert expected positive and negative results.
 *
 * <p>The direct method benchmarks use {@code pattern=NAMED,NAME_SET,REGULAR_NAMED} over Spring Web
 * and Reactor declared methods (including constructors, excluding type initializers, which Byte
 * Buddy omits). These model common advice-selection patterns separately from class selection. Setup
 * pairs methods by name and descriptor and checks both APIs agree for every pattern and method,
 * requiring hits and misses. Class and method states have independent parameters and setup. Method
 * graphs are constructed once, then reused throughout JVM warmup and measurement. SHARED mixes only
 * method graphs for direct method execution, and only class graphs for class execution. These are
 * synthetic mixes, not production frequency weights. The small method mix can expose a change from
 * two receiver types (which HotSpot may inline) to three (which it may leave as virtual dispatch),
 * so a shared-score regression need not predict the cost in a more diverse workload.
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
 * <p>DD_TRACE selects the large graph extracted from dd-trace-java, including six labelled
 * regular-method annotation projections. It excludes hierarchy and loader gates. The {@code
 * executeClassMatcher} and {@code executeByteBuddyMatcher} methods measure the OR union; {@code
 * sweepClassMatcher} and {@code sweepByteBuddyMatcher} evaluate every branch on one class and
 * return the number of hits. Both use the same corpus, validation, ISOLATED/SHARED profiles, and
 * ns/op units. A sweep score includes all branches, not one predicate. Fixture expectations for the
 * small graphs do not apply to DD_TRACE; its branches are validated against Byte Buddy and their
 * union. SHARED includes DD_TRACE in the workload mix.
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
 * class or method matched). Results return the match boolean or sweep hit count to JMH. With {@code
 * profile=SHARED}, scores report average time and allocation across the entire graph mix. The
 * selected workload changes its weighting in that mix; scores do not measure the selected graph
 * alone under shared profiling.
 *
 * <p>Use {@code 'MatchingApiBenchmark.execute(ClassMatcher|ByteBuddyMatcher)'} to select class
 * execution, or {@code 'MatchingApiBenchmark.execute(MethodMatcher|ByteBuddyMethodMatcher)'} for
 * direct methods. Use {@code 'MatchingApiBenchmark.sweep(ClassMatcher|ByteBuddyMatcher)'} for
 * complete branch sweeps. Select the large graph with {@code -p workload=DD_TRACE -p
 * profile=ISOLATED}. Select method patterns with {@code -p pattern=NAMED}; select class graphs with
 * {@code -p workload=COMBINED}. For a quick validation run, add {@code -wi 1 -i 1 -w 200ms -r 200ms
 * -f 1 -t 1}; use the normal settings for performance comparisons.
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

  @State(Scope.Thread)
  public static class ClassMatching {
    @Param({
      "REAL_WORLD",
      "CLASS_SELECTION",
      "TYPE_NAMES",
      "STRUCTURAL",
      "STRUCTURAL_SIGNATURES",
      "COMBINED",
      "DD_TRACE"
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
    private ClassMatcher[][] branches;
    private ElementMatcher.Junction<TypeDescription>[][] byteBuddyBranches;
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
              new ClassFileLocator.Simple(corpus),
              ClassFileLocator.ForClassLoader.ofSystemLoader());
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
      branches = new ClassMatcher[workloads.length][];
      byteBuddyBranches = new ElementMatcher.Junction[workloads.length][];
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
          if (workloads[j] != MatcherWorkload.DD_TRACE
              && fixtureExpected != null
              && expected != (workloads[j] == MatcherWorkload.COMBINED && fixtureExpected)) {
            throw new IllegalStateException(
                "Unexpected fixture result: " + workloads[j] + " / " + names[i]);
          }
          boolean[] selectionExpected = selectionExpectations.get(names[i]);
          if (workloads[j] != MatcherWorkload.DD_TRACE
              && selectionExpected != null
              && expected != SelectionFixtures.expected(selectionExpected, workloads[j])) {
            throw new IllegalStateException(
                "Unexpected selection fixture: " + workloads[j] + " / " + names[i]);
          }
          hits[j] += expected ? 1 : 0;
          boolean branchUnion = false;
          for (int k = 0; k < branches[j].length; k++) {
            boolean branchMatch = branches[j][k].test(outlines[i]);
            if (byteBuddyBranches[j][k].matches(descriptions[i]) != branchMatch) {
              throw new IllegalStateException(
                  "Branch disagreement: " + workloads[j] + "[" + k + "] / " + names[i]);
            }
            branchHits[j][k] += branchMatch ? 1 : 0;
            branchUnion |= branchMatch;
          }
          if (branchUnion != expected) {
            throw new IllegalStateException(
                "Union disagrees with branches: " + workloads[j] + " / " + names[i]);
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
          if ((branchHits[i][k] == 0 && workloads[i] != MatcherWorkload.DD_TRACE)
              || branchHits[i][k] == names.length) {
            throw new IllegalStateException(
                "Branch needs both hits and misses: " + workloads[i] + "[" + k + "]");
          }
        }
      }
      System.out.printf(
          "%s / %s: %d classes, %d hits, %d misses for selected graph; %d graphs%n",
          profile, workload, names.length, hits[0], names.length - hits[0], workloads.length);
      for (int i = 0; i < workloads.length; i++) {
        if (workloads[i] == MatcherWorkload.DD_TRACE) {
          int covered = 0;
          long totalHits = 0;
          for (int count : branchHits[i]) {
            covered += count > 0 ? 1 : 0;
            totalHits += count;
          }
          // Many library-specific selectors have no positive examples in this application corpus.
          System.out.printf(
              "DD_TRACE: %d branches x %d classes = %d comparisons; %d branch hits; "
                  + "%d branches with hits; %d union hits%n",
              branches[i].length,
              names.length,
              (long) branches[i].length * names.length,
              totalHits,
              covered,
              hits[i]);
        }
      }
      if (workload != MatcherWorkload.DD_TRACE) {
        System.out.println(
            "Branch hits (in graph factory order): " + Arrays.toString(branchHits[0]));
      }
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

  /** State and corpus for direct advice method selection, independent of class-selection params. */
  @State(Scope.Thread)
  public static class MethodMatching {
    @Param({"NAMED", "NAME_SET", "REGULAR_NAMED"})
    public MethodWorkload pattern;

    /** Same weighting as class selection: selected pattern twice, every other pattern once. */
    @Param({"ISOLATED", "SHARED"})
    public String profile;

    private MethodOutline[] outlines;
    private MethodDescription[] descriptions;
    private MethodMatcher[] methodMatchers;
    private ElementMatcher.Junction<MethodDescription>[] byteBuddyMatchers;
    private int cursor;
    private int matcherIndex;

    @Setup
    @SuppressWarnings("unchecked")
    public void setup() {
      MethodWorkload[] patterns;
      if ("SHARED".equals(profile)) {
        MethodWorkload[] all = MethodWorkload.values();
        patterns = new MethodWorkload[all.length + 1];
        patterns[0] = pattern;
        System.arraycopy(all, 0, patterns, 1, all.length);
      } else {
        patterns = new MethodWorkload[] {pattern};
      }
      methodMatchers = new MethodMatcher[patterns.length];
      byteBuddyMatchers = new ElementMatcher.Junction[patterns.length];
      for (int i = 0; i < patterns.length; i++) {
        methodMatchers[i] = ClassMatcherGraphs.create(patterns[i]);
        byteBuddyMatchers[i] = ByteBuddyGraphs.create(patterns[i]);
      }
      Map<String, byte[]> corpus = new LinkedHashMap<>();
      for (String jar : new String[] {"spring-web.jar", "reactor-core.jar"}) {
        for (byte[] bytecode : SampleClasses.loadBytecode(jar)) {
          String name = ClassFile.header(bytecode).className.replace('/', '.');
          if (!name.equals("module-info")) {
            corpus.putIfAbsent(name, bytecode);
          }
        }
      }
      ClassFileLocator locator =
          new ClassFileLocator.Compound(
              new ClassFileLocator.Simple(corpus),
              ClassFileLocator.ForClassLoader.ofSystemLoader());
      TypePool pool =
          new TypePool.Default.WithLazyResolution(
              new TypePool.CacheProvider.Simple(), locator, TypePool.Default.ReaderMode.FAST);
      List<MethodOutline> methodOutlines = new ArrayList<>();
      List<MethodDescription> methodDescriptions = new ArrayList<>();
      for (Map.Entry<String, byte[]> entry : corpus.entrySet()) {
        Map<String, MethodOutline> declared = new LinkedHashMap<>();
        for (MethodOutline method : ClassFile.outline(entry.getValue()).methods) {
          // Byte Buddy's declared-method list excludes type initializers.
          if (!method.methodName.equals(ClassFile.STATIC_INITIALIZER)) {
            declared.put(method.methodName + method.descriptor, method);
          }
        }
        for (MethodDescription method :
            pool.describe(entry.getKey()).resolve().getDeclaredMethods()) {
          String signature = method.getInternalName() + method.getDescriptor();
          MethodOutline outline = declared.remove(signature);
          if (outline == null) {
            throw new IllegalStateException(
                "Missing outline: " + entry.getKey() + " / " + signature);
          }
          methodOutlines.add(outline);
          methodDescriptions.add(method);
        }
        if (!declared.isEmpty()) {
          throw new IllegalStateException(
              "Missing Byte Buddy methods: " + entry.getKey() + declared.keySet());
        }
      }
      outlines = methodOutlines.toArray(new MethodOutline[0]);
      descriptions = methodDescriptions.toArray(new MethodDescription[0]);
      int[] hits = new int[patterns.length];
      for (int i = 0; i < outlines.length; i++) {
        for (int j = 0; j < patterns.length; j++) {
          boolean actual = methodMatchers[j].test(outlines[i]);
          if (actual != byteBuddyMatchers[j].matches(descriptions[i])) {
            throw new IllegalStateException(
                "Method matcher disagreement: " + patterns[j] + " / " + descriptions[i]);
          }
          hits[j] += actual ? 1 : 0;
        }
      }
      for (int i = 0; i < patterns.length; i++) {
        if (hits[i] == 0 || hits[i] == outlines.length) {
          throw new IllegalStateException("Pattern needs both hits and misses: " + patterns[i]);
        }
      }
      System.out.printf(
          "%s / %s: %d methods, %d hits for selected pattern; %d graphs%n",
          profile, pattern, outlines.length, hits[0], patterns.length);
    }

    private int nextMethod() {
      int index = cursor;
      if (index + 1 == outlines.length) {
        cursor = 0;
        matcherIndex = matcherIndex + 1 == methodMatchers.length ? 0 : matcherIndex + 1;
      } else {
        cursor = index + 1;
      }
      return index;
    }
  }

  /** Evaluate every branch, including branches hidden by earlier matches in the union. */
  @Benchmark
  public int sweepClassMatcher(ClassMatching state) {
    ClassMatcher[] matchers = state.branches[state.matcherIndex];
    ClassOutline outline = state.outlines[state.nextClass()];
    int hits = 0;
    for (ClassMatcher matcher : matchers) {
      hits += matcher.test(outline) ? 1 : 0;
    }
    return hits;
  }

  @Benchmark
  public int sweepByteBuddyMatcher(ClassMatching state) {
    ElementMatcher.Junction<TypeDescription>[] matchers =
        state.byteBuddyBranches[state.matcherIndex];
    TypeDescription description = state.descriptions[state.nextClass()];
    int hits = 0;
    for (ElementMatcher.Junction<TypeDescription> matcher : matchers) {
      hits += matcher.matches(description) ? 1 : 0;
    }
    return hits;
  }

  @Benchmark
  public boolean executeMethodMatcher(MethodMatching state) {
    MethodMatcher matcher = state.methodMatchers[state.matcherIndex];
    return matcher.test(state.outlines[state.nextMethod()]);
  }

  @Benchmark
  public boolean executeByteBuddyMethodMatcher(MethodMatching state) {
    ElementMatcher.Junction<MethodDescription> matcher =
        state.byteBuddyMatchers[state.matcherIndex];
    return matcher.matches(state.descriptions[state.nextMethod()]);
  }

  @Benchmark
  public boolean executeClassMatcher(ClassMatching state) {
    ClassMatcher matcher = state.classMatchers[state.matcherIndex];
    return matcher.test(state.outlines[state.nextClass()]);
  }

  @Benchmark
  public boolean executeByteBuddyMatcher(ClassMatching state) {
    ElementMatcher.Junction<TypeDescription> matcher = state.byteBuddyMatchers[state.matcherIndex];
    return matcher.matches(state.descriptions[state.nextClass()]);
  }
}
