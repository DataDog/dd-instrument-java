/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.fieldinject;

import static java.util.concurrent.TimeUnit.SECONDS;

import com.blogspot.mydailyjava.weaklockfree.WeakConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.GroupThreads;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

/**
 * Measures whether contention on unrelated {@link ObjectStore}s spills over onto a store nobody
 * else is touching. A per-store map shares nothing, so it should show no difference between running
 * alone and running alongside a contended pool. {@link ObjectStore} is instead backed by {@link
 * GlobalObjectStore}, which picks a shard by fibonacci-hashing {@code storeId}; a contended store
 * landing on the same shard as the isolated store (via a storeId hash collision, or a shared
 * ageing/eviction pass) could degrade its throughput.
 *
 * <p>The contended pool uses a distinct key type per store; since the shard hash mixes both halves
 * of {@code storeId}, varying either key type or value type spreads store-ids across shards.
 *
 * <p>Each variant runs the isolated store alone as a baseline ({@code isolatedStoreAlone_*}), then
 * again alongside a disjoint contended pool hammered by more threads ({@code
 * isolatedStoreUnderLoad_*} paired with {@code contendedPool_*} in a JMH {@code @Group}). Re-run
 * this when tuning {@code GlobalObjectStore}'s shard count/selection or backing map capacity: less
 * spillover should shrink the gap between {@code isolatedStoreAlone_globalObjectStore} and {@code
 * isolatedStoreUnderLoad_globalObjectStore}.
 *
 * <pre>
 *   ./gradlew :field-inject:jmh -Pjmh.includes=ObjectStoreContentionBenchmark
 * </pre>
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(SECONDS)
@Fork(3)
@Warmup(iterations = 2)
@Measurement(iterations = 5)
@State(Scope.Benchmark)
@SuppressWarnings("unused")
public class ObjectStoreContentionBenchmark {

  private static final int CONTENDED_STORE_COUNT = 4;
  private static final int CONTENDED_GROUP_THREADS = 7;
  private static final int ISOLATED_GROUP_THREADS = 1;

  // Well below GlobalObjectStore's shard limit so this measures hashing/locking, not eviction.
  private static final int CONTENDED_KEY_BUDGET = 6400;
  private static final int ISOLATED_KEY_POOL_SIZE = 4096;

  private static final Object CONTEXT = new Object();

  private ObjectStore<Object, Object>[] contendedGlobalStores;
  private ObjectStore<Object, Object> isolatedGlobalStore;

  private PerStoreMap[] contendedPerStoreMaps;
  private PerStoreMap isolatedPerStoreMap;

  private final AtomicInteger nextContendedThreadIndex = new AtomicInteger();

  @Setup(Level.Trial)
  @SuppressWarnings("unchecked")
  public void setupTrial() {
    contendedGlobalStores = new ObjectStore[CONTENDED_STORE_COUNT];
    contendedPerStoreMaps = new PerStoreMap[CONTENDED_STORE_COUNT];
    for (int i = 0; i < CONTENDED_STORE_COUNT; i++) {
      // Distinct key type per store, mirroring real shard spread (see class javadoc).
      contendedGlobalStores[i] = ObjectStore.of("ContendedKey" + i, "ContendedValue" + i);
      contendedPerStoreMaps[i] = new PerStoreMap();
    }
    isolatedGlobalStore = ObjectStore.of("IsolatedKey", "IsolatedValue");
    isolatedPerStoreMap = new PerStoreMap();
  }

  private static KeyCursor newKeyCursor(int size) {
    Object[] keys = new Object[size];
    for (int i = 0; i < size; i++) {
      keys[i] = new Object();
    }
    return new KeyCursor(keys);
  }

  private static final class KeyCursor {
    private final Object[] keys;
    private int cursor;

    KeyCursor(Object[] keys) {
      this.keys = keys;
    }

    Object next() {
      cursor = (cursor + 1) % keys.length;
      return keys[cursor];
    }
  }

  @State(Scope.Thread)
  public static class ContendedGlobalState {
    private ObjectStore<Object, Object> store;
    private KeyCursor keyCursor;

    @Setup(Level.Trial)
    public void setup(ObjectStoreContentionBenchmark benchmark) {
      int threadIndex = benchmark.nextContendedThreadIndex.getAndIncrement();
      store = benchmark.contendedGlobalStores[threadIndex % CONTENDED_STORE_COUNT];
      keyCursor = newKeyCursor(Math.max(1, CONTENDED_KEY_BUDGET / CONTENDED_GROUP_THREADS));
    }
  }

  @State(Scope.Thread)
  public static class ContendedPerStoreState {
    private PerStoreMap store;
    private KeyCursor keyCursor;

    @Setup(Level.Trial)
    public void setup(ObjectStoreContentionBenchmark benchmark) {
      int threadIndex = benchmark.nextContendedThreadIndex.getAndIncrement();
      store = benchmark.contendedPerStoreMaps[threadIndex % CONTENDED_STORE_COUNT];
      keyCursor = newKeyCursor(Math.max(1, CONTENDED_KEY_BUDGET / CONTENDED_GROUP_THREADS));
    }
  }

  @State(Scope.Thread)
  public static class IsolatedGlobalState {
    private KeyCursor keyCursor;

    @Setup(Level.Trial)
    public void setup() {
      keyCursor = newKeyCursor(ISOLATED_KEY_POOL_SIZE);
    }
  }

  @State(Scope.Thread)
  public static class IsolatedPerStoreState {
    private KeyCursor keyCursor;

    @Setup(Level.Trial)
    public void setup() {
      keyCursor = newKeyCursor(ISOLATED_KEY_POOL_SIZE);
    }
  }

  private static void putGetRemove(ObjectStore<Object, Object> store, KeyCursor keyCursor) {
    Object key = keyCursor.next();
    store.put(key, CONTEXT);
    store.get(key);
    store.remove(key);
  }

  private static void putGetRemove(PerStoreMap store, KeyCursor keyCursor) {
    Object key = keyCursor.next();
    store.put(key, CONTEXT);
    store.get(key);
    store.remove(key);
  }

  // --- Baseline: the isolated store with zero interference from any other thread. ---

  @Benchmark
  @Threads(ISOLATED_GROUP_THREADS)
  public void isolatedStoreAlone_globalObjectStore(IsolatedGlobalState state) {
    putGetRemove(isolatedGlobalStore, state.keyCursor);
  }

  @Benchmark
  @Threads(ISOLATED_GROUP_THREADS)
  public void isolatedStoreAlone_mapPerStore(IsolatedPerStoreState state) {
    putGetRemove(isolatedPerStoreMap, state.keyCursor);
  }

  // --- Mixed: the isolated store, plus a disjoint contended pool hammered concurrently. ---
  // ISOLATED_GROUP_THREADS + CONTENDED_GROUP_THREADS must sum to the benchmark's total threads.

  @Benchmark
  @Group("mixedGlobalObjectStore")
  @GroupThreads(ISOLATED_GROUP_THREADS)
  public void isolatedStoreUnderLoad_globalObjectStore(IsolatedGlobalState state) {
    putGetRemove(isolatedGlobalStore, state.keyCursor);
  }

  @Benchmark
  @Group("mixedGlobalObjectStore")
  @GroupThreads(CONTENDED_GROUP_THREADS)
  public void contendedPool_globalObjectStore(ContendedGlobalState state) {
    putGetRemove(state.store, state.keyCursor);
  }

  @Benchmark
  @Group("mixedMapPerStore")
  @GroupThreads(ISOLATED_GROUP_THREADS)
  public void isolatedStoreUnderLoad_mapPerStore(IsolatedPerStoreState state) {
    putGetRemove(isolatedPerStoreMap, state.keyCursor);
  }

  @Benchmark
  @Group("mixedMapPerStore")
  @GroupThreads(CONTENDED_GROUP_THREADS)
  public void contendedPool_mapPerStore(ContendedPerStoreState state) {
    putGetRemove(state.store, state.keyCursor);
  }

  /** Genuine per-store map: nothing here is ever shared with any other store. */
  private static final class PerStoreMap {
    private final WeakConcurrentMap<Object, Object> map = new WeakConcurrentMap<>(false, true);

    Object get(Object key) {
      return map.get(key);
    }

    void put(Object key, Object value) {
      map.put(key, value);
    }

    Object remove(Object key) {
      return map.remove(key);
    }
  }
}
