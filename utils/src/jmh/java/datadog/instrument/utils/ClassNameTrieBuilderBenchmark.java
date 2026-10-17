package datadog.instrument.utils;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

@State(Scope.Benchmark)
@BenchmarkMode(AverageTime)
@OutputTimeUnit(MILLISECONDS)
@Fork(
    value = 3,
    jvmArgsAppend = {"-Xms512m", "-Xmx512m"})
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Threads(1)
public class ClassNameTrieBuilderBenchmark {

  @Param({"exclusions", "classNames", "longJumps"})
  public String dataset;

  private String[] keys;
  private int[] values;

  @Setup
  public void setup() throws IOException {
    List<String> names = new ArrayList<>();
    List<Integer> numbers = new ArrayList<>();
    switch (dataset) {
      case "exclusions":
        ClassNameTrieBenchmark.buildTrieEntries(
            (name, value) -> {
              names.add(name);
              numbers.add(value);
            });
        break;
      case "classNames":
        names.addAll(Files.readAllLines(Paths.get("src/test/resources/sample_class_names.txt")));
        break;
      case "longJumps":
        char[] chars = new char[7700];
        Arrays.fill(chars, 'p');
        String padding = new String(chars);
        for (int group = 1; group <= 8; group++) {
          for (int branch = 1; branch <= 8; branch++) {
            names.add("" + (char) group + '\u0001' + (char) branch + padding);
          }
          for (int branch = 2; branch <= 65; branch++) {
            char smallChar = (char) (branch <= 41 ? branch : branch + 1); // skip '*'
            names.add("" + (char) group + smallChar);
          }
        }
        break;
      default:
        throw new IllegalArgumentException("Unknown dataset: " + dataset);
    }
    keys = names.toArray(new String[0]);
    values = new int[keys.length];
    for (int i = 0; i < values.length; i++) {
      values[i] = numbers.isEmpty() ? 1 : numbers.get(i);
    }
  }

  @Benchmark
  public ClassNameTrie.Builder build() {
    ClassNameTrie.Builder builder = new ClassNameTrie.Builder();
    for (int i = 0; i < keys.length; i++) {
      builder.put(keys[i], values[i]);
    }
    return builder;
  }
}
