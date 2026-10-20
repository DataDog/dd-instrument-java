package datadog.instrument.classmatch;

import static datadog.instrument.classmatch.InternalMatchers.internalName;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

import datadog.instrument.testing.SampleClasses;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/** Reports time per corpus class matched against every sampled group, without short-circuiting. */
@State(Scope.Thread)
@BenchmarkMode(AverageTime)
@OutputTimeUnit(NANOSECONDS)
@Fork(3)
@Warmup(iterations = 4, time = 1)
@Measurement(iterations = 6, time = 1)
public class InternalNamesBenchmark {
  private static final int CLASS_COUNT = 33957;

  @Param({"STRING", "TYPE_STRING"})
  public String queryType;

  private InternalNames[] groups;
  private CharSequence[] queries;

  @Setup
  public void setup() throws IOException {
    String[] classNames =
        SampleClasses.loadClassNames().stream()
            .map(InternalMatchers::internalName)
            .toArray(String[]::new);
    queries = Arrays.stream(classNames).map(this::query).toArray(CharSequence[]::new);
    // OperationsPerInvocation must match the shared corpus size.
    if (queries.length != CLASS_COUNT) {
      throw new IllegalStateException("Update CLASS_COUNT for the shared corpus");
    }

    List<InternalNames> sets = new ArrayList<>();
    long matches = 0;
    try (InputStream in =
            requireNonNull(
                InternalNamesBenchmark.class.getResourceAsStream("type-name-groups.csv"),
                "type-name-groups.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, UTF_8))) {
      for (String line; (line = reader.readLine()) != null; ) {
        if (line.isEmpty() || line.startsWith("#")) {
          continue;
        }
        String[] columns = line.split(",");
        InternalNames names = new InternalNames(Arrays.asList(columns).subList(1, columns.length));
        sets.add(names);
        Set<String> expected = new HashSet<>();
        for (int i = 1; i < columns.length; i++) {
          expected.add(internalName(columns[i]));
        }
        // Validate hits even for group members absent from the shared corpus.
        for (String name : expected) {
          if (!names.containsType(query(name))) {
            throw new IllegalStateException("Missing group member: " + name);
          }
        }
        for (int i = 0; i < queries.length; i++) {
          CharSequence query = queries[i];
          boolean hit = expected.contains(classNames[i]);
          if (names.containsType(query) != hit) {
            throw new IllegalStateException("Incorrect corpus membership: " + query);
          }
          matches += hit ? 1 : 0;
        }
      }
    }
    if (sets.isEmpty()) {
      throw new IllegalStateException("No sampled groups");
    }
    groups = sets.toArray(new InternalNames[0]);
    System.out.printf(
        "Corpus: %d classes, %d groups, %d hits%n", queries.length, groups.length, matches);
  }

  private CharSequence query(String name) {
    // Both query forms have their hashes computed outside measurement.
    int hash = name.hashCode();
    if (queryType.equals("STRING")) {
      return name;
    }
    String descriptor = "L" + name + ";";
    return new TypeString(descriptor, 1, descriptor.length() - 1, hash);
  }

  @Benchmark
  @OperationsPerInvocation(CLASS_COUNT)
  public long matchAllGroups() {
    long matches = 0;
    for (CharSequence query : queries) {
      for (InternalNames names : groups) {
        matches += names.containsType(query) ? 1 : 0;
      }
    }
    return matches;
  }
}
