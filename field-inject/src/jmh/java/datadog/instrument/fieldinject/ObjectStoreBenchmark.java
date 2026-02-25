package datadog.instrument.fieldinject;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
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

/**
 * This benchmark randomly populates a series of object-stores to different target occupancies.
 * Halfway through the benchmark the keys are regenerated to exercise eviction of stale keys.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(MICROSECONDS)
@Warmup(iterations = 0)
@Measurement(iterations = 1, time = 10, timeUnit = SECONDS)
@SuppressWarnings({"unused", "rawtypes", "unchecked"})
public class ObjectStoreBenchmark {

  @Param({"true", "false"})
  public boolean mapPerStore;

  @State(Scope.Benchmark)
  public static class TrialData {

    @Param({"10000", "50000", "100000", "150000"})
    public int targetGlobalOccupancy;

    public List<Integer> seedRequestIds;

    @Setup(Level.Trial)
    public void setup() {
      // request ids are decoded into store-id (lower bits) and a key-index (upper bits)
      // this lets us distribute the target global occupancy uniformly across all stores
      seedRequestIds = IntStream.range(0, targetGlobalOccupancy).boxed().collect(toList());
    }
  }

  @State(Scope.Thread)
  public static class ThreadData {

    public int[] randomRequestIds;

    public int requestIndex = 0;

    @Setup(Level.Iteration)
    public void setupThread(TrialData trialData) {
      // randomize the request order per-thread
      List<Integer> copy = new ArrayList<>(trialData.seedRequestIds);
      Collections.shuffle(copy);
      randomRequestIds = copy.stream().mapToInt(Integer::intValue).toArray();
    }
  }

  @Benchmark
  @Threads(value = 1)
  public void singleThreaded(Blackhole blackhole, ThreadData threadData) {
    batchedCompute(blackhole, threadData);
  }

  @Benchmark
  @Threads(value = 10)
  public void multiThreaded(Blackhole blackhole, ThreadData threadData) {
    batchedCompute(blackhole, threadData);
  }

  private static final int NUM_STORES = 10;

  private static final ObjectStore[] stores = new ObjectStore[NUM_STORES];

  static {
    Arrays.setAll(stores, store -> ObjectStore.of("KeyType", "ValueType$" + store));
  }

  private static final WeakObjectMap[] maps = new WeakObjectMap[NUM_STORES];

  static {
    Arrays.setAll(maps, store -> new WeakObjectMap());
  }

  // create enough keys to cover max target occupancy per-store
  private static final Object[] keys = new Object[150_000 / NUM_STORES];

  static {
    generateKeys();
  }

  private static final Function<Object, Object> allocator = key -> new byte[1024];

  @Setup
  public void setup() {
    Thread cleanerThread =
        new Thread(
            () -> {
              // attempt to remove stale entries from all stores every second
              for (int cycle = 1; ; cycle++) {
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
                if (cycle % 10 == 5) {
                  generateKeys(); // regenerate keys half-way through benchmark
                  System.gc();
                }
              }
            });
    cleanerThread.setDaemon(true);
    cleanerThread.start();
  }

  static void generateKeys() {
    Arrays.setAll(keys, i -> "Key_" + i);
  }

  // batches 1_000 computes as a single JMH operation
  private void batchedCompute(Blackhole blackhole, ThreadData threadData) {
    int i = threadData.requestIndex;
    for (int lim = i + 1_000; i < lim; i++) {
      blackhole.consume(compute(threadData.randomRequestIds[i]));
    }
    // setup next call to use a new batch of request ids
    threadData.requestIndex = i % threadData.randomRequestIds.length;
  }

  // distributes compute requests over stores/keys
  private Object compute(int requestId) {
    // decode request into store and key
    int store = requestId % NUM_STORES;
    Object key = keys[requestId / NUM_STORES];
    if (mapPerStore) {
      return maps[store].computeIfAbsent(key, allocator);
    } else {
      return stores[store].getOrCompute(key, allocator);
    }
  }

  /**
   * Provides similar object-store semantics using a {@link WeakConcurrentMap} for a single store.
   */
  static final class WeakObjectMap<K, V> {
    // total capacity over all per-map stores should equal GlobalObjectStore's hard limit
    private static final int MAX_SIZE = 100_000 / NUM_STORES;

    private final WeakConcurrentMap<Object, Object> map = new WeakConcurrentMap<>(false, true);

    public void expungeStaleEntries() {
      map.expungeStaleEntries();
    }

    public V get(final K key) {
      return (V) map.get(key);
    }

    public void put(final K key, final V context) {
      if (map.approximateSize() < MAX_SIZE) {
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
