package datadog.instrument.utils;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

import java.util.List;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(AverageTime)
@OutputTimeUnit(MICROSECONDS)
@SuppressWarnings("unused")
public abstract class AbstractClassLoaderBenchmark {

  @Param({"10", "300", "800"})
  public int classLoaderCount;

  private List<ClassLoader> classLoaders;

  private ToIntFunction<ClassLoader> function;

  @Setup(Level.Trial)
  public void setup() {
    classLoaders =
        IntStream.range(0, classLoaderCount)
            .mapToObj(i -> new ClassLoader() {})
            .collect(Collectors.toList());

    function = classLoaderFunction();
  }

  @Benchmark
  @Fork(value = 1)
  @Threads(value = 1)
  public void singleThreaded(Blackhole blackhole) {
    test(blackhole);
  }

  @Benchmark
  @Fork(value = 1)
  @Threads(value = 10)
  public void multiThreaded(Blackhole blackhole) {
    test(blackhole);
  }

  private void test(Blackhole blackhole) {
    blackhole.consume(classLoaders.stream().mapToInt(function).sum());
  }

  abstract ToIntFunction<ClassLoader> classLoaderFunction();
}
