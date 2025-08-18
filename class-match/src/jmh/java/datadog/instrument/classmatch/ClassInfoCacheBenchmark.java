package datadog.instrument.classmatch;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

import java.net.URL;
import java.net.URLClassLoader;
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
public class ClassInfoCacheBenchmark {

  @Param({"64", "256", "1024", "4096", "16384"})
  public int cacheSize;

  private List<String> classNames;

  private ClassLoader cl;

  private ClassInfoCache<Object> cache;

  private Object infoToShare;

  @Setup(Level.Trial)
  public void setup() {
    cl = URLClassLoader.newInstance(new URL[0]);

    classNames =
        SampleClasses.load().stream()
            .map(bytecode -> ClassFile.header(bytecode).className)
            .collect(Collectors.toList());

    cache = new ClassInfoCache<>(cacheSize);

    infoToShare = new Object();
  }

  @Benchmark
  @Fork(value = 1)
  @Threads(value = 1)
  public void test1(Blackhole blackhole) {
    test(blackhole);
  }

  @Benchmark
  @Fork(value = 1)
  @Threads(value = 10)
  public void test10(Blackhole blackhole) {
    test(blackhole);
  }

  @Benchmark
  @Fork(value = 1)
  @Threads(value = 100)
  public void test100(Blackhole blackhole) {
    test(blackhole);
  }

  private void test(Blackhole blackhole) {
    for (String name : classNames) {
      if (cache.find(name, cl) == null) {
        cache.share(name, infoToShare, cl);
      }
    }
  }
}
