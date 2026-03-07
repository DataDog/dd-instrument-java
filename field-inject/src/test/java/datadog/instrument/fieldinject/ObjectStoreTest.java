/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.fieldinject;

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
