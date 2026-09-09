package datadog.instrument.fieldinject;

import static datadog.instrument.fieldinject.GlobalObjectStore.AGEING_THRESHOLD;
import static datadog.instrument.fieldinject.GlobalObjectStore.SHARD_COUNT;
import static datadog.instrument.fieldinject.GlobalObjectStore.SHARD_HARD_LIMIT;
import static datadog.instrument.fieldinject.GlobalObjectStore.SHARD_SOFT_LIMIT;
import static datadog.instrument.fieldinject.ObjectStoreIds.objectStoreId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ObjectStoreTest {

  // Each test uses this store; unique type names avoid cross-test interference
  private ObjectStore<Object, String> store;

  @BeforeEach
  void setUp() {
    store = ObjectStore.of("test.ObjectStore.Key", "test.ObjectStore.Value");
    // Drain any stale entries left by previous tests
    ObjectStore.removeStaleEntries();
  }

  // --- get ---

  @Test
  void getReturnsNullForAbsentKey() {
    assertNull(store.get(new Object()));
  }

  // --- put / get ---

  @Test
  void putThenGet() {
    Object key = new Object();
    store.put(key, "hello");
    assertEquals("hello", store.get(key));
  }

  @Test
  void putNullRemovesEntry() {
    Object key = new Object();
    store.put(key, "hello");
    store.put(key, null);
    assertNull(store.get(key));
  }

  @Test
  void putOverwritesExistingValue() {
    Object key = new Object();
    store.put(key, "first");
    store.put(key, "second");
    assertEquals("second", store.get(key));
  }

  // --- remove ---

  @Test
  void removeReturnsAndClearsEntry() {
    Object key = new Object();
    store.put(key, "hello");
    assertEquals("hello", store.remove(key));
    assertNull(store.get(key));
  }

  @Test
  void removeReturnsNullForAbsentKey() {
    assertNull(store.remove(new Object()));
  }

  // --- getOrPut ---

  @Test
  void getOrPutReturnsExistingValue() {
    Object key = new Object();
    store.put(key, "existing");
    assertEquals("existing", store.getOrPut(key, "new"));
    assertEquals("existing", store.get(key));
  }

  @Test
  void getOrPutInsertsWhenAbsent() {
    Object key = new Object();
    assertEquals("new", store.getOrPut(key, "new"));
    assertEquals("new", store.get(key));
  }

  // --- getOrCompute ---

  @Test
  void getOrComputeReturnsExistingValue() {
    Object key = new Object();
    store.put(key, "existing");
    assertEquals("existing", store.getOrCompute(key, k -> "computed"));
    assertEquals("existing", store.get(key));
  }

  @Test
  void getOrComputeInsertsWhenAbsent() {
    Object key = new Object();
    assertEquals("computed", store.getOrCompute(key, k -> "computed"));
    assertEquals("computed", store.get(key));
  }

  @Test
  void getOrComputePassesCorrectKeyToFunction() {
    Object key = new Object();
    AtomicReference<Object> received = new AtomicReference<>();
    store.getOrCompute(
        key,
        k -> {
          received.set(k);
          return "value";
        });
    assertSame(key, received.get());
  }

  // --- isolation ---

  @Test
  void differentKeysHaveIndependentEntries() {
    Object key1 = new Object();
    Object key2 = new Object();
    store.put(key1, "v1");
    store.put(key2, "v2");
    assertEquals("v1", store.get(key1));
    assertEquals("v2", store.get(key2));
  }

  @Test
  void sameKeyInDifferentStoresIsIsolated() {
    ObjectStore<Object, String> storeA = ObjectStore.of("test.IsolA.Key", "test.IsolA.Value");
    ObjectStore<Object, String> storeB = ObjectStore.of("test.IsolB.Key", "test.IsolB.Value");
    Object key = new Object();
    storeA.put(key, "a");
    storeB.put(key, "b");
    assertEquals("a", storeA.get(key));
    assertEquals("b", storeB.get(key));

    storeA.remove(key);
    assertNull(storeA.get(key));
    assertEquals("b", storeB.get(key));
  }

  // --- factory method consistency ---

  @Test
  void ofWithSameNamesSharesState() {
    ObjectStore<Object, String> s1 = ObjectStore.of("test.Shared.Key", "test.Shared.Value");
    ObjectStore<Object, String> s2 = ObjectStore.of("test.Shared.Key", "test.Shared.Value");
    Object key = new Object();
    s1.put(key, "shared");
    assertEquals("shared", s2.get(key));
  }

  @Test
  void ofWithClassTypesMatchesOfWithTypeNames() {
    ObjectStore<String, Integer> byClass = ObjectStore.of(String.class, Integer.class);
    ObjectStore<String, Integer> byName =
        ObjectStore.of(String.class.getName(), Integer.class.getName());
    String key = "factory-test-key";
    byClass.put(key, 99);
    assertEquals(99, byName.get(key));
  }

  // --- GC cleanup ---

  @Test
  void keyIsCollectedByGcWhenNoLongerReferenced() throws InterruptedException {
    WeakReference<Object> ref = putWithEphemeralKey(store, "value");
    gcUntil(() -> ref.get() == null);
    assertNull(ref.get(), "Key should have been garbage collected");
  }

  @Test
  void removeStaleEntriesReducesSizeAfterKeyGc() throws InterruptedException {
    ObjectStore<Object, String> gcStore = ObjectStore.of("test.GcSize.Key", "test.GcSize.Value");
    int sizeBeforePut = ObjectStore.removeStaleEntries();
    int count = 20;

    List<WeakReference<Object>> refs = putManyWithEphemeralKeys(gcStore, count);

    // Stale entries still occupy the map until explicitly cleaned up
    gcUntil(() -> ObjectStore.removeStaleEntries() <= sizeBeforePut);

    assertTrue(
        refs.stream().map(WeakReference::get).allMatch(Objects::isNull),
        "Keys should have been garbage collected");
  }

  @Test
  void liveEntriesArePreservedByRemoveStaleEntries() {
    Object key = new Object();
    store.put(key, "alive");

    ObjectStore.removeStaleEntries();

    assertEquals("alive", store.get(key), "Live entry should not be removed");
  }

  @Test
  void removeStaleEntriesIsIdempotent() {
    Object key = new Object();
    store.put(key, "stable");

    ObjectStore.removeStaleEntries();
    ObjectStore.removeStaleEntries();

    assertEquals("stable", store.get(key));
  }

  // --- generational capacity / eviction ---

  @Test
  void sustainedInsertionIsBoundedByAgeingAndSoftLimitTrim() {
    ObjectStore<Object, Integer> capStore =
        ObjectStore.of("test.Capacity.Key", "test.Capacity.Value");

    // removeStaleEntries() sums estimated size across all shards; only this store's shard is
    // populated here, so the sum reflects that one shard directly.
    int otherShardsBaseline = ObjectStore.removeStaleEntries();

    // Insert enough distinct, strongly-referenced keys to drive the young generation past
    // AGEING_THRESHOLD several times over, landing mid-cycle (comfortably above the soft
    // limit) so both inline ageing and removeStaleEntries' soft-limit trim get exercised.
    int totalInserts = (AGEING_THRESHOLD * 4) + 8_000;
    List<Object> keys = new ArrayList<>(totalInserts);
    for (int i = 0; i < totalInserts; i++) {
      Object key = new Object();
      keys.add(key);
      capStore.put(key, i);
    }

    // Inline enforceCapacity keeps young+old from ever exceeding the shard hard limit by ageing
    // young into old before that point is reached, so recently inserted keys must still be
    // retrievable even after many multiples of the shard's capacity have been inserted.
    Object lastKey = keys.get(keys.size() - 1);
    assertEquals(totalInserts - 1, capStore.get(lastKey));

    int finalSize = ObjectStore.removeStaleEntries() - otherShardsBaseline;
    assertTrue(
        finalSize < SHARD_HARD_LIMIT,
        "Sustained insertion should have triggered eviction rather than unbounded growth, observed "
            + finalSize);
    assertTrue(
        finalSize <= SHARD_SOFT_LIMIT,
        "removeStaleEntries should trim content back to the shard soft limit, observed "
            + finalSize);
  }

  @Test
  void survivingKeyIsShadowedAfterAgeingAndRemoveClearsBothGenerations() {
    ObjectStore<Object, Integer> genStore =
        ObjectStore.of("test.GenerationBoundary.Key", "test.GenerationBoundary.Value");

    Object survivorKey = new Object();
    genStore.put(survivorKey, -1);

    // Insert enough further distinct, strongly-referenced keys to push the young generation past
    // AGEING_THRESHOLD (ageing survivorKey's entry into the old generation), while staying well
    // short of a second ageing cycle that would drop it again.
    int keysToForceAgeing = AGEING_THRESHOLD + 10_000;
    List<Object> keys = new ArrayList<>(keysToForceAgeing);
    for (int i = 0; i < keysToForceAgeing; i++) {
      Object key = new Object();
      keys.add(key);
      genStore.put(key, i);
    }

    Object lastBulkKey = keys.get(keys.size() - 1);
    assertEquals(keysToForceAgeing - 1, genStore.get(lastBulkKey));

    // survivorKey's original entry should now live in the old generation, but must still resolve.
    assertEquals(-1, genStore.get(survivorKey));

    // Overwriting writes into the (new) young generation, shadowing the stale old-generation entry.
    genStore.put(survivorKey, 42);
    assertEquals(42, genStore.get(survivorKey), "put should shadow the stale old-generation entry");

    // remove() must clear both generations; otherwise the old entry would resurrect the old value.
    assertEquals(42, genStore.remove(survivorKey));
    assertNull(
        genStore.get(survivorKey),
        "remove must clear the old-generation copy too, or it would resurrect the shadowed value");
  }

  @Test
  void singleThreadCyclingAcrossAllShardsStillSamplesEachShard() {
    // A thread writing to several stores in a fixed round-robin cycle whose length divides
    // SIZE_SAMPLE_RATE (1024) should sample capacity on every shard it visits, not just one.
    GlobalObjectStore[] chosenShards = new GlobalObjectStore[SHARD_COUNT];
    String[] chosenKeyTypes = new String[SHARD_COUNT];
    int found = 0;
    int suffix = 0;
    while (found < SHARD_COUNT) {
      String candidate = "test.ShardCycle.Key" + suffix++;
      GlobalObjectStore shard =
          GlobalObjectStore.shard(objectStoreId(candidate, "test.ShardCycle.Value"));
      boolean alreadyChosen = false;
      for (int i = 0; i < found; i++) {
        if (chosenShards[i] == shard) {
          alreadyChosen = true;
          break;
        }
      }
      if (!alreadyChosen) {
        chosenShards[found] = shard;
        chosenKeyTypes[found] = candidate;
        found++;
      }
    }

    ObjectStore<Object, Integer>[] cycleStores = new ObjectStore[SHARD_COUNT];
    for (int i = 0; i < SHARD_COUNT; i++) {
      cycleStores[i] = ObjectStore.of(chosenKeyTypes[i], "test.ShardCycle.Value");
    }

    int baseline = ObjectStore.removeStaleEntries();

    int perStoreInserts = SHARD_HARD_LIMIT + AGEING_THRESHOLD;
    int totalInserts = perStoreInserts * SHARD_COUNT;
    List<Object> keys = new ArrayList<>(totalInserts);
    for (int i = 0; i < totalInserts; i++) {
      Object key = new Object();
      keys.add(key);
      cycleStores[i % SHARD_COUNT].put(key, i);
    }

    Object lastKey = keys.get(keys.size() - 1);
    assertEquals(totalInserts - 1, cycleStores[(totalInserts - 1) % SHARD_COUNT].get(lastKey));

    // If any shard were starved of sampling it would never age or trim, so its young map would
    // grow to hold roughly its entire share of inserts (perStoreInserts) instead of settling near
    // the soft limit; that dwarfs a healthy aggregate across all shards.
    int aggregate = ObjectStore.removeStaleEntries() - baseline;
    int healthyCeiling = SHARD_COUNT * SHARD_SOFT_LIMIT;
    assertTrue(
        aggregate <= healthyCeiling,
        "Every shard touched by this cyclic pattern should sample and age independently; "
            + "observed aggregate size "
            + aggregate
            + " exceeds "
            + healthyCeiling
            + " (a starved shard would grow unbounded instead of ageing)");
  }

  @Test
  void oneShardsSustainedLoadDoesNotAgeOrEvictAnotherShard() {
    // Pick two key types landing on different shards, matching ObjectStoreShardingTest's approach.
    String loadedKeyType = "test.ShardIsolation.LoadedKey";
    int loadedStoreId = objectStoreId(loadedKeyType, "test.ShardIsolation.Value");
    String quietKeyType = null;
    int suffix = 0;
    while (true) {
      String candidate = "test.ShardIsolation.QuietKey" + suffix;
      if (GlobalObjectStore.shard(objectStoreId(candidate, "test.ShardIsolation.Value"))
          != GlobalObjectStore.shard(loadedStoreId)) {
        quietKeyType = candidate;
        break;
      }
      suffix++;
    }

    ObjectStore<Object, String> quietStore =
        ObjectStore.of(quietKeyType, "test.ShardIsolation.Value");
    Object quietKey = new Object();
    quietStore.put(quietKey, "still here");

    ObjectStore<Object, Integer> loadedStore =
        ObjectStore.of(loadedKeyType, "test.ShardIsolation.Value");
    int totalInserts = (AGEING_THRESHOLD * 4) + 8_000;
    List<Object> keys = new ArrayList<>(totalInserts);
    for (int i = 0; i < totalInserts; i++) {
      Object key = new Object();
      keys.add(key);
      loadedStore.put(key, i);
    }

    Object lastKey = keys.get(keys.size() - 1);
    assertEquals(totalInserts - 1, loadedStore.get(lastKey));

    // The loaded shard aged/evicted repeatedly, but the quiet shard's entry must be untouched.
    assertEquals("still here", quietStore.get(quietKey));
  }

  // --- concurrency ---

  @Test
  void concurrentPutsOnDistinctKeysAreAllVisible() throws InterruptedException {
    ObjectStore<Object, String> concStore =
        ObjectStore.of("test.Concurrent.Key", "test.Concurrent.Value");
    int threads = 8;
    int perThread = 100;
    Object[][] keys = new Object[threads][perThread];
    for (int t = 0; t < threads; t++) {
      for (int i = 0; i < perThread; i++) {
        keys[t][i] = new Object();
      }
    }

    CountDownLatch start = new CountDownLatch(1);
    CountDownLatch done = new CountDownLatch(threads);
    ExecutorService executor = Executors.newFixedThreadPool(threads);
    try {
      for (int t = 0; t < threads; t++) {
        final int threadIdx = t;
        executor.submit(
            () -> {
              try {
                start.await();
                for (int i = 0; i < perThread; i++) {
                  concStore.put(keys[threadIdx][i], "v" + threadIdx + "-" + i);
                }
              } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
              } finally {
                done.countDown();
              }
            });
      }
      start.countDown();
      assertThat(done.await(10, TimeUnit.SECONDS)).isTrue();
    } finally {
      executor.shutdown();
    }

    for (int t = 0; t < threads; t++) {
      for (int i = 0; i < perThread; i++) {
        assertEquals("v" + t + "-" + i, concStore.get(keys[t][i]));
      }
    }
  }

  @Test
  void concurrentSustainedInsertionAcrossAgeingIsRaceFree() throws InterruptedException {
    // maybeAgeStore() uses a per-shard CAS token so only one thread ages a shard at a time; this
    // drives several threads through repeated ageing on the same shard to check that races there
    // don't lose recently written entries.
    ObjectStore<Object, Integer> concCapStore =
        ObjectStore.of("test.ConcurrentCapacity.Key", "test.ConcurrentCapacity.Value");

    int threads = 8;
    int perThread = (AGEING_THRESHOLD * 4 + 8_000) / threads;

    // Tracks whichever write actually finishes last. A thread's own last write is not a safe
    // thing to check here: other threads may still have thousands of writes left, which can
    // legitimately age it out by design.
    AtomicReference<Object[]> lastWrite = new AtomicReference<>();

    CountDownLatch start = new CountDownLatch(1);
    CountDownLatch done = new CountDownLatch(threads);
    ExecutorService executor = Executors.newFixedThreadPool(threads);
    try {
      for (int t = 0; t < threads; t++) {
        final int threadIdx = t;
        executor.submit(
            () -> {
              try {
                start.await();
                for (int i = 0; i < perThread; i++) {
                  Object key = new Object();
                  int value = (threadIdx * perThread) + i;
                  concCapStore.put(key, value);
                  lastWrite.set(new Object[] {key, value});
                }
              } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
              } finally {
                done.countDown();
              }
            });
      }
      start.countDown();
      assertThat(done.await(30, TimeUnit.SECONDS)).isTrue();
    } finally {
      executor.shutdown();
    }

    // The write that actually finished last has nothing written after it: if a concurrent ageing
    // race ever let two threads swap generations at once, it could still land in a generation
    // that gets discarded instead of becoming the new old generation.
    Object[] lastKeyValuePair = lastWrite.get();
    assertEquals(lastKeyValuePair[1], concCapStore.get(lastKeyValuePair[0]));
  }

  // --- helpers ---

  /**
   * Puts a value into the store using a key allocated inside this method. Returning a {@link
   * WeakReference} to it means the key is no longer strongly reachable after the call, making it
   * eligible for GC.
   */
  private static WeakReference<Object> putWithEphemeralKey(
      ObjectStore<Object, String> store, String value) {
    Object key = new Object();
    store.put(key, value);
    return new WeakReference<>(key);
  }

  /** Calls {@link #putWithEphemeralKey} {@code count} times. */
  private static List<WeakReference<Object>> putManyWithEphemeralKeys(
      ObjectStore<Object, String> store, int count) {
    List<WeakReference<Object>> refs = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      refs.add(putWithEphemeralKey(store, "v" + i));
    }
    return refs;
  }

  private static final long GC_WAIT_NANOS = TimeUnit.SECONDS.toNanos(30);

  /** Repeatedly triggers GC until the given flag is true, up to a short timeout. */
  private static void gcUntil(BooleanSupplier flag) throws InterruptedException {
    System.gc();
    final long start = System.nanoTime();
    while (!flag.getAsBoolean()) {
      if (System.nanoTime() - start > GC_WAIT_NANOS) {
        throw new RuntimeException("Timed out waiting for GC");
      }
      if (Thread.interrupted()) {
        throw new InterruptedException();
      }
      System.gc();
      //noinspection BusyWait
      Thread.sleep(100);
    }
  }
}
