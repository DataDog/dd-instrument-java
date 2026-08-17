/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.fieldinject;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;

/**
 * Global key-value store used when field-injection is not possible. Since the same object may
 * participate in multiple stores each global key captures the store identity along with a weak
 * reference to the owning key object.
 *
 * <p>The store is split into two maps with separate reference queues: young and old. Ageing the
 * store by one generation creates a new young map; the previous young map becomes the old map.
 */
public final class GlobalObjectStore {

  /** Target ceiling for the total number of objects in the global store, young and old. */
  private static final int GLOBAL_HARD_LIMIT = 100_000;

  /** Threshold at which we age the current store by one generation. */
  private static final int AGEING_THRESHOLD = GLOBAL_HARD_LIMIT / 2;

  /** Target ceiling for the total number of objects allowed after background eviction. */
  private static final int GLOBAL_SOFT_LIMIT = (GLOBAL_HARD_LIMIT + AGEING_THRESHOLD) / 2;

  /** Threshold at which we start doing limited cleanup at the same time as put operations. */
  private static final int INLINE_CLEANUP_THRESHOLD = 5_000;

  /** Constant supplier used when nothing in the store is considered old. */
  private static final Supplier<Object> NO_OLD_KEYS = () -> null;

  /** The current generation of the global object store. */
  private static volatile GlobalObjectStore store = new GlobalObjectStore();

  /** Token used to decide which thread gets to age the store. */
  private static final AtomicBoolean ageing = new AtomicBoolean();

  // the following fields represent a generation of the object store

  /** Supplies store keys where the key object is unused and eligible for collection. */
  private final ReferenceQueue<Object> staleKeys = new ReferenceQueue<>();

  /** Map of weak store keys to value objects. */
  private final ConcurrentHashMap<StoreKey, Object> map;

  /** Supplies old keys where the key object is unused and eligible for collection. */
  private final Supplier<Object> oldStaleKeys;

  /** Map of old store keys to value objects. */
  private final Map<StoreKey, Object> oldMap;

  private GlobalObjectStore() {
    this.map = new ConcurrentHashMap<>();
    this.oldStaleKeys = NO_OLD_KEYS;
    this.oldMap = Collections.emptyMap();
  }

  private GlobalObjectStore(GlobalObjectStore oldStore) {
    this.map = new ConcurrentHashMap<>(INLINE_CLEANUP_THRESHOLD);
    this.oldStaleKeys = oldStore.staleKeys::poll;
    this.oldMap = oldStore.map;
  }

  /**
   * Removes stale entries from the global object-store, where the key object is now unused.
   *
   * <p>It is the caller's responsibility to decide how often to call {@code #removeStaleEntries}.
   * It may be periodically with a background thread, on certain requests, or some other condition.
   *
   * @return the estimated remaining size of the global object-store
   */
  public static int removeStaleEntries() {
    return store.doRemoveStaleEntries();
  }

  private int doRemoveStaleEntries() {
    Object staleKey;

    // first remove stale entries from the young map
    while ((staleKey = staleKeys.poll()) != null) {
      //noinspection All: we know staleKey is a store key
      map.remove(staleKey);
    }

    // next remove stale entries from the old map
    while ((staleKey = oldStaleKeys.get()) != null) {
      //noinspection All: we know staleKey is a store key
      oldMap.remove(staleKey);
    }

    int estimatedSize = map.size() + oldMap.size();

    // randomly evict old content to keep us below the soft limit
    Iterator<StoreKey> itr = oldMap.keySet().iterator();
    while (estimatedSize >= GLOBAL_SOFT_LIMIT && itr.hasNext()) {
      itr.next();
      itr.remove();
      estimatedSize--;
    }

    return estimatedSize;
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
    return store.doGet(key, storeId);
  }

  @Nullable
  private Object doGet(Object key, int storeId) {
    LookupKey lookupKey = LookupKey.with(key, storeId);
    try {
      //noinspection All: intentionally use lookup key without reference overhead
      Object value = map.get(lookupKey);
      //noinspection All: intentionally use lookup key without reference overhead
      return value != null ? value : oldMap.get(lookupKey);
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
    GlobalObjectStore s = store;
    if (value == null) {
      s.doRemove(key, storeId);
    } else {
      s.checkCapacity().doPut(key, storeId, value);
    }
  }

  private void doPut(Object key, int storeId, Object value) {
    map.put(new StoreKey(staleKeys, key, storeId), value);
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
    GlobalObjectStore s = store;
    Object existing = s.doGet(key, storeId); // avoids creating unnecessary store key
    if (existing != null || value == null) {
      return existing;
    } else {
      return s.checkCapacity().doGetOrPut(key, storeId, value);
    }
  }

  private Object doGetOrPut(Object key, int storeId, Object value) {
    Object existing = map.putIfAbsent(new StoreKey(staleKeys, key, storeId), value);
    return existing != null ? existing : value;
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
  @SuppressWarnings({"rawtypes"})
  public static Object getOrCompute(Object key, int storeId, Function valueFunction) {
    GlobalObjectStore s = store;
    Object existing = s.doGet(key, storeId); // avoids creating unnecessary store key
    if (existing != null) {
      return existing;
    } else {
      return s.checkCapacity().doGetOrCompute(key, storeId, valueFunction);
    }
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private Object doGetOrCompute(Object key, int storeId, Function valueFunction) {
    return map.computeIfAbsent(
        new StoreKey(staleKeys, key, storeId), unused -> valueFunction.apply(key));
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
    return store.doRemove(key, storeId);
  }

  @Nullable
  private Object doRemove(Object key, int storeId) {
    LookupKey lookupKey = LookupKey.with(key, storeId);
    try {
      //noinspection All: intentionally use lookup key without reference overhead
      Object value = map.remove(lookupKey);
      //noinspection All: intentionally use lookup key without reference overhead
      Object oldValue = oldMap.remove(lookupKey);
      return value != null ? value : oldValue;
    } finally {
      lookupKey.reset();
    }
  }

  /**
   * Checks store capacity, performing inline eviction or ageing if appropriate.
   *
   * @return the latest generation of the global store
   */
  private GlobalObjectStore checkCapacity() {
    int youngSize = map.size();
    if (youngSize >= INLINE_CLEANUP_THRESHOLD) {
      Object staleKey = staleKeys.poll();
      if (staleKey != null) {
        //noinspection All: we know staleKey is a store key
        map.remove(staleKey);
      }
      if (youngSize >= AGEING_THRESHOLD) {
        return maybeAgeStore();
      }
    }
    return this;
  }

  /**
   * Attempts to age this store by one generation; if already ageing don't block, use latest.
   *
   * @return the latest generation of the global store
   */
  @SuppressFBWarnings("ST") // we want to update the global object store
  private GlobalObjectStore maybeAgeStore() {
    // first try to get the token that allows us to age the global store
    boolean attemptAgeing = ageing.compareAndSet(false, true);
    // only after this get the latest generation of the store
    GlobalObjectStore s = store;
    if (attemptAgeing) {
      try {
        if (s == this) {
          // our store is still the latest; go ahead and age it
          s = store = new GlobalObjectStore(this);
        }
      } finally {
        ageing.set(false); // relinquish the token
      }
    }
    return s; // always return the latest generation of the store
  }

  /** Key used to weakly associate a non-injected key and store-id with a value. */
  private static final class StoreKey extends WeakReference<Object> {

    final int hash;
    final int storeId;

    StoreKey(ReferenceQueue<Object> staleKeys, Object key, int storeId) {
      super(key, staleKeys);
      this.hash = (31 * storeId) + System.identityHashCode(key);
      this.storeId = storeId;
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
