/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.fieldinject;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import javax.annotation.Nullable;

/**
 * Global key-value store used when field-injection is not possible. Since the same object may
 * participate in multiple stores each global key captures the store identity along with a weak
 * reference to the original object key.
 */
public final class GlobalObjectStore {

  /** Never allow more than this number of objects in the global store. */
  private static final int GLOBAL_HARD_LIMIT = 100_000;

  /** Threshold at which we age the young generation into the old generation. */
  private static final int AGEING_THRESHOLD = GLOBAL_HARD_LIMIT / 2;

  /** Temporarily allow more than this number of objects, but start removing old content. */
  private static final int GLOBAL_SOFT_LIMIT = (GLOBAL_HARD_LIMIT + AGEING_THRESHOLD) / 2;

  /** Threshold at which we start doing limited cleanup at the same time as put operations. */
  private static final int INLINE_CLEANUP_THRESHOLD = 5_000;

  /** Bounds any inline eviction attempts. */
  private static final int MAX_INLINE_EVICTION_ATTEMPTS = 10;

  private static final Object staleEntriesLock = new Object();

  /** Tracks young and old content in the object store. */
  private static final class Generations {
    final ConcurrentHashMap<StoreKey, Object> young;
    final ConcurrentHashMap<StoreKey, Object> old;

    Generations(ConcurrentHashMap<StoreKey, Object> old) {
      this.young = new ConcurrentHashMap<>(old.size());
      this.old = old;
    }
  }

  private static volatile Generations generations = new Generations(new ConcurrentHashMap<>());

  private static final AtomicBoolean ageing = new AtomicBoolean();

  private GlobalObjectStore() {}

  /**
   * Removes stale entries from the global object-store, where the key object is now unused.
   *
   * <p>It is the caller's responsibility to decide how often to call {@code #removeStaleEntries}.
   * It may be periodically with a background thread, on certain requests, or some other condition.
   *
   * @return the estimated remaining size of the global object-store
   */
  public static int removeStaleEntries() {
    synchronized (staleEntriesLock) {
      StoreKey key;
      while ((key = StoreKey.pollStaleKeys()) != null) {
        removeEntry(key);
      }

      Generations g = generations;

      int estimatedSize = g.young.size() + g.old.size();

      // proactively remove old content when above the soft limit
      Iterator<StoreKey> itr = g.old.keySet().iterator();
      while (estimatedSize >= GLOBAL_SOFT_LIMIT && itr.hasNext()) {
        itr.next();
        itr.remove();
        estimatedSize--;
      }

      return estimatedSize;
    }
  }

  /**
   * Gets the value currently associated with the given key and store-id.
   *
   * @param key the key
   * @param storeId the store-id
   * @return value associated with the key; {@code null} if there is no value
   */
  @Nullable
  public static Object get(Object key, int storeId) {
    LookupKey lookupKey = LookupKey.with(key, storeId);
    try {
      Generations g = generations;
      //noinspection All: intentionally use lookup key without reference overhead
      Object value = g.young.get(lookupKey);
      if (value == null) {
        //noinspection All: intentionally use lookup key without reference overhead
        value = g.old.get(lookupKey);
      }
      return value;
    } finally {
      lookupKey.reset();
    }
  }

  /**
   * Associates the given key and store-id with the given value.
   *
   * @param key the key
   * @param storeId the store-id
   * @param value the new value
   */
  public static void put(Object key, int storeId, @Nullable Object value) {
    if (value == null) {
      remove(key, storeId);
    } else {
      enforceCapacity();
      generations.young.put(new StoreKey(key, storeId), value);
    }
  }

  /**
   * Gets the value currently associated with the given key and store-id. If no value exists then
   * associate the key and store-id with the given value and return that.
   *
   * @param key the key
   * @param storeId the store-id
   * @param value the new value
   * @return existing value if present, otherwise the new value
   */
  public static Object getOrPut(Object key, int storeId, @Nullable Object value) {
    Object existing = get(key, storeId);
    if (existing != null || value == null) {
      return existing;
    } else {
      enforceCapacity();
      existing = generations.young.putIfAbsent(new StoreKey(key, storeId), value);
      return existing != null ? existing : value;
    }
  }

  /**
   * Gets the value currently associated with the given key and store-id. If no value exists then
   * associate the key and store-id with a value computed by the given function and return that.
   *
   * @param key the key
   * @param storeId the store-id
   * @param valueFunction function to compute values from keys
   * @return existing value if present, otherwise the new computed value
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static Object getOrCompute(Object key, int storeId, Function valueFunction) {
    Object existing = get(key, storeId);
    if (existing != null) {
      return existing;
    } else {
      enforceCapacity();
      return generations.young.computeIfAbsent(
          new StoreKey(key, storeId), unused -> valueFunction.apply(key));
    }
  }

  /**
   * Removes the value associated with the given key and store-id.
   *
   * @param key the key
   * @param storeId the store-id
   * @return value previously associated with the key; {@code null} if there was no value
   */
  @Nullable
  public static Object remove(Object key, int storeId) {
    LookupKey lookupKey = LookupKey.with(key, storeId);
    try {
      return removeEntry(lookupKey);
    } finally {
      lookupKey.reset();
    }
  }

  private static Object removeEntry(Object key) {
    Generations g = generations;
    //noinspection All: intentionally use lookup key without reference overhead
    Object youngValue = g.young.remove(key);
    //noinspection All: intentionally use lookup key without reference overhead
    Object oldValue = g.old.remove(key);
    return youngValue != null ? youngValue : oldValue;
  }

  private static void enforceCapacity() {
    Generations g = generations;
    int youngSize = g.young.size();
    int totalSize = youngSize + g.old.size();
    if (totalSize < INLINE_CLEANUP_THRESHOLD) {
      return; // skip inline eviction
    }

    // attempt a single stale eviction
    StoreKey staleKey;
    int attempts = MAX_INLINE_EVICTION_ATTEMPTS;
    while (attempts-- > 0 && (staleKey = StoreKey.pollStaleKeys()) != null) {
      if (removeEntry(staleKey) != null) {
        return;
      }
    }

    // when the young generation maxes out, age it so it becomes old
    if (youngSize >= AGEING_THRESHOLD && ageGenerations(g)) {
      return;
    }

    // attempt a single old eviction
    if (totalSize >= GLOBAL_HARD_LIMIT) {
      evictOldKey();
    }
  }

  private static boolean ageGenerations(Generations g) {
    if (ageing.compareAndSet(false, true)) {
      try {
        if (g == generations) {
          generations = new Generations(g.young);
          return true;
        }
      } finally {
        ageing.set(false);
      }
    }
    return false;
  }

  private static void evictOldKey() {
    Generations g = generations;
    int attempts = MAX_INLINE_EVICTION_ATTEMPTS;
    Iterator<StoreKey> itr = g.old.keySet().iterator();
    while (attempts-- > 0 && itr.hasNext()) {
      if (g.old.remove(itr.next()) != null) {
        return;
      }
    }
  }

  /** Key used to weakly associate a non-injected key and store-id with a value. */
  private static final class StoreKey extends WeakReference<Object> {

    // stale store keys where the key object is unused and eligible for collection
    private static final ReferenceQueue<Object> staleKeys = new ReferenceQueue<>();

    final int hash;
    final int storeId;

    StoreKey(Object key, int storeId) {
      super(key, staleKeys);
      this.hash = (31 * storeId) + System.identityHashCode(key);
      this.storeId = storeId;
    }

    static StoreKey pollStaleKeys() {
      return (StoreKey) staleKeys.poll();
    }

    @Override
    public int hashCode() {
      return hash;
    }

    @Override
    @SuppressFBWarnings("Eq") // symmetric because it mirrors LookupKey.equals
    public boolean equals(Object o) {
      if (o instanceof LookupKey) {
        LookupKey lookupKey = (LookupKey) o;
        return storeId == lookupKey.storeId && get() == lookupKey.key;
      } else if (o instanceof StoreKey) {
        StoreKey storeKey = (StoreKey) o;
        return storeId == storeKey.storeId && get() == storeKey.get();
      } else {
        return false;
      }
    }
  }

  /** Temporary key used for lookup purposes without the reference tracking overhead. */
  private static final class LookupKey {

    /** Avoid allocation by maintaining a reusable lookup key per-thread. */
    private static final ThreadLocal<LookupKey> LOOKUP_KEY_CACHE =
        ThreadLocal.withInitial(LookupKey::new);

    Object key;
    int hash;
    int storeId;

    /**
     * Returns a temporary lookup key for the current thread with the given object key and store-id.
     * This key must be reset by calling {@link #reset} as soon as the get/remove request completes.
     *
     * @param key the key
     * @param storeId the store-id
     * @return temporary key that can only be used to get or remove values from the global map
     */
    static LookupKey with(Object key, int storeId) {
      LookupKey thiz = LOOKUP_KEY_CACHE.get();
      thiz.key = key;
      thiz.hash = (31 * storeId) + System.identityHashCode(key);
      thiz.storeId = storeId;
      return thiz;
    }

    /** Resets this temporary lookup key so it can be reused in a future get/remove request. */
    void reset() {
      this.key = null; // only need to clear the object key so it can be collected
    }

    @Override
    public int hashCode() {
      return hash;
    }

    @Override
    @SuppressFBWarnings("Eq") // symmetric because it mirrors StoreKey.equals
    public boolean equals(Object o) {
      if (o instanceof StoreKey) {
        StoreKey storeKey = (StoreKey) o;
        return storeId == storeKey.storeId && key == storeKey.get();
      } else if (o instanceof LookupKey) {
        LookupKey lookupKey = (LookupKey) o;
        return storeId == lookupKey.storeId && key == lookupKey.key;
      } else {
        return false;
      }
    }
  }
}
