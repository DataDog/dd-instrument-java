package datadog.instrument.classmatch;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.openjdk.jmh.annotations.Mode.SingleShotTime;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/**
 * First construction of an instrumentation-style matcher graph, compared with Byte Buddy. Each
 * API/workload runs in ten fresh JVM forks with no warmup and one graph construction per fork.
 * There is no corpus setup or preceding graph construction. The equivalent factories in {@link
 * ClassMatcherGraphs} and {@link ByteBuddyGraphs} are shared with {@link MatchingApiBenchmark},
 * which validates their matching results. Each factory class only references its own API, so
 * neither measurement includes loading the other library.
 *
 * <p>From the repository root, build with {@code ./gradlew :class-match:jmhJar}, then run:
 *
 * <pre>{@code
 * cd class-match
 * mkdir -p build/results/jmh
 * "$JAVA_17_HOME/bin/java" -jar build/libs/class-match-*-jmh.jar \
 *   ColdMatcherConstructionBenchmark -foe true -prof gc \
 *   -rf json -rff build/results/jmh/construction-cold.json
 * }</pre>
 *
 * <p>The default workloads distinguish common class selection, type-name predicates, rare
 * structural probes, and the COMBINED API-coverage graph. STRUCTURAL_SIGNATURES expresses the same
 * structural probes using complete signatures. Neither type-name nor structural projections perform
 * hierarchy traversal or resolve a field's class metadata.
 *
 * <p>Replace JAVA_17_HOME to compare Java 8 and other LTS JVMs. Add {@code -p workload=COMBINED} to
 * select the combined graph; CLASS_SELECTION selects the common real-world projections. Keep zero
 * warmup, one measurement iteration, and batch size one to preserve first-use behavior; increase
 * the fork count for more samples.
 *
 * <p>Time is reported in ms per graph and includes first-use matcher class initialization and
 * lambda linkage, excluding JVM process startup. Graphs are returned to JMH so they escape. The GC
 * profiler reports bytes per operation, including first-use initialization and iteration
 * infrastructure overhead; these are not steady-state graph sizes.
 */
@State(Scope.Thread)
@BenchmarkMode(SingleShotTime)
@OutputTimeUnit(MILLISECONDS)
@Fork(
    value = 10,
    jvmArgsAppend = {"-Xms512m", "-Xmx512m"})
@Warmup(iterations = 0)
@Measurement(iterations = 1, batchSize = 1)
public class ColdMatcherConstructionBenchmark {
  @Param({
    "REAL_WORLD",
    "CLASS_SELECTION",
    "TYPE_NAMES",
    "STRUCTURAL",
    "STRUCTURAL_SIGNATURES",
    "COMBINED"
  })
  public MatcherWorkload workload;

  // Object return types keep matcher types out of this benchmark's reflected signatures.
  @Benchmark
  public Object createClassMatcher() {
    return ClassMatcherGraphs.create(workload);
  }

  @Benchmark
  public Object createByteBuddyMatcher() {
    return ByteBuddyGraphs.create(workload);
  }
}
