package datadog.instrument.classmatch;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

import datadog.instrument.testing.SampleClasses;
import java.util.List;
import java.util.stream.Collectors;
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
public class ClassNameFilterBenchmark {

  @Param({"64", "4096", "16384", "65536", "1048576"})
  public int cacheSize;

  private List<String> classNames;

  private ClassNameFilter cache;

  @Setup(Level.Trial)
  public void setup() {

    classNames =
        SampleClasses.load("spring-web.jar").stream()
            .map(bytecode -> ClassFile.header(bytecode).className)
            .collect(Collectors.toList());

    cache = new ClassNameFilter(cacheSize);
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
    for (String name : classNames) {
      if (!cache.contains(name)) {
        cache.add(name);
      }
    }
  }
}
