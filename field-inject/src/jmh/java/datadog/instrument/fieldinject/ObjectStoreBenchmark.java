package datadog.instrument.fieldinject;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.stream.Collectors.toList;

import com.blogspot.mydailyjava.weaklockfree.WeakConcurrentMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(MICROSECONDS)
@Warmup(iterations = 0)
@Measurement(iterations = 1)
@SuppressWarnings({"unused", "rawtypes", "unchecked"})
public class ObjectStoreBenchmark {

  @Param({"true", "false"})
  public boolean mapPerStore;

  @State(Scope.Benchmark)
  public static class TrialData {

    @Param({"10000", "50000", "100000", "150000"})
    public int maxOccupancy;

    public List<Integer> seedIds;

    @Setup(Level.Trial)
    public void setup() {
      seedIds = IntStream.range(0, maxOccupancy).boxed().collect(toList());
    }
  }

  @State(Scope.Thread)
  public static class ThreadData {

    public int[] randomIds;

    public int index = 0;

    @Setup(Level.Iteration)
    public void setupThread(TrialData trialData) {
      List<Integer> copy = new ArrayList<>(trialData.seedIds);
      Collections.shuffle(copy);
      randomIds = copy.stream().mapToInt(Integer::intValue).toArray();
    }
  }

  @Benchmark
  @Threads(value = 1)
  public void singleThreaded(Blackhole blackhole, ThreadData threadData) {
    test(blackhole, threadData);
  }

  @Benchmark
  @Threads(value = 10)
  public void multiThreaded(Blackhole blackhole, ThreadData threadData) {
    test(blackhole, threadData);
  }

  private static final String[] keys = new String[15_0000];

  static {
    refreshKeys();
  }

  private static final Function<String, byte[]> allocator = key -> new byte[1024];

  private static final ObjectStore[] stores = {
    ObjectStore.of("java.lang.String", "Type_0"),
    ObjectStore.of("java.lang.String", "Type_1"),
    ObjectStore.of("java.lang.String", "Type_2"),
    ObjectStore.of("java.lang.String", "Type_3"),
    ObjectStore.of("java.lang.String", "Type_4"),
    ObjectStore.of("java.lang.String", "Type_5"),
    ObjectStore.of("java.lang.String", "Type_6"),
    ObjectStore.of("java.lang.String", "Type_7"),
    ObjectStore.of("java.lang.String", "Type_8"),
    ObjectStore.of("java.lang.String", "Type_9")
  };

  private static final WeakObjectMap[] maps = {
    new WeakObjectMap(),
    new WeakObjectMap(),
    new WeakObjectMap(),
    new WeakObjectMap(),
    new WeakObjectMap(),
    new WeakObjectMap(),
    new WeakObjectMap(),
    new WeakObjectMap(),
    new WeakObjectMap(),
    new WeakObjectMap()
  };

  @Setup
  public void setup() {
    Thread cleanerThread =
        new Thread(
            () -> {
              while (true) {
                if (mapPerStore) {
                  for (WeakObjectMap map : maps) {
                    map.expungeStaleEntries();
                  }
                } else {
                  ObjectStore.removeStaleEntries();
                }
                try {
                  //noinspection BusyWait
                  Thread.sleep(1_000);
                } catch (InterruptedException e) {
                  break;
                }
                refreshKeys();
              }
            });
    cleanerThread.setDaemon(true);
    cleanerThread.start();
  }

  static void refreshKeys() {
    Arrays.setAll(keys, i -> "Key_" + i);
  }

  private void test(Blackhole blackhole, ThreadData threadData) {
    int i = threadData.index;
    for (int lim = i + 1_000; i < lim; i++) {
      blackhole.consume(compute(threadData.randomIds[i]));
    }
    threadData.index = i % threadData.randomIds.length;
  }

  private Object compute(int id) {
    int store = id % 10;
    String key = keys[id / 10];
    if (mapPerStore) {
      return maps[store].computeIfAbsent(key, allocator);
    } else {
      return stores[store].getOrCompute(key, allocator);
    }
  }

  static final class WeakObjectMap<K, V> {
    private static final int DEFAULT_MAX_SIZE = 10_000;

    private final int maxSize;
    private final WeakConcurrentMap<Object, Object> map = new WeakConcurrentMap<>(false, true);

    public WeakObjectMap(int maxSize) {
      this.maxSize = maxSize;
    }

    public WeakObjectMap() {
      this(DEFAULT_MAX_SIZE);
    }

    public void expungeStaleEntries() {
      map.expungeStaleEntries();
    }

    public V get(final K key) {
      return (V) map.get(key);
    }

    public void put(final K key, final V context) {
      if (map.approximateSize() < maxSize) {
        map.put(key, context);
      }
    }

    public V putIfAbsent(final K key, final V context) {
      V existingContext = get(key);
      if (null == existingContext) {
        synchronized (map) {
          existingContext = get(key);
          if (null == existingContext) {
            existingContext = context;
            put(key, existingContext);
          }
        }
      }
      return existingContext;
    }

    public V computeIfAbsent(K key, Function<? super K, V> factory) {
      V existingContext = get(key);
      if (null == existingContext) {
        synchronized (map) {
          existingContext = get(key);
          if (null == existingContext) {
            existingContext = factory.apply(key);
            put(key, existingContext);
          }
        }
      }
      return existingContext;
    }

    public V remove(final K key) {
      return (V) map.remove(key);
    }
  }
}
