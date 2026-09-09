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
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;

/**
 * Global sharded key-value store used when field-injection is not possible. Since the same object
 * may participate in multiple stores each global key captures the store identity along with a weak
 * reference to the owning key object.
 *
 * <p>Each shard is split into two maps with separate reference queues: young and old. Ageing a
 * shard by one generation creates a new young map; the previous young map becomes the old map.
 */
public final class GlobalObjectStore {

  /** Target ceiling for the total number of objects in a shard, young and old. */
  static final int SHARD_HARD_LIMIT = 32_000;

  /** Threshold at which we age a shard by one generation. */
  static final int AGEING_THRESHOLD = SHARD_HARD_LIMIT / 2;

  /** Target ceiling for total number of objects allowed in a shard after background eviction. */
  static final int SHARD_SOFT_LIMIT = (SHARD_HARD_LIMIT + AGEING_THRESHOLD) / 2;

  /** Shift used to pick a shard from a store-id after fibonacci-hashing. */
  private static final int SHARD_BITS = 3;

  /** Number of independent shards; store-ids are spread across shards. */
  static final int SHARD_COUNT = 1 << SHARD_BITS;

  /** Threshold at which we start doing limited cleanup at the same time as put operations. */
  private static final int INLINE_CLEANUP_THRESHOLD = 2_000;

  /** Randomly sample underlying map size, approximately once every 1024 writes per-thread. */
  private static final int SIZE_SAMPLE_RATE = 1 << 10;

  /** Constant supplier used when nothing in a shard is considered old. */
  private static final Supplier<Object> NO_OLD_STALE_KEYS = () -> null;

  /** The current generation of each shard of the global object store. */
  private static final AtomicReferenceArray<GlobalObjectStore> shards = initShards();

  /** Per-shard token used to decide which thread gets to age that shard. */
  private static final AtomicIntegerArray ageing = new AtomicIntegerArray(SHARD_COUNT);

  private static AtomicReferenceArray<GlobalObjectStore> initShards() {
    AtomicReferenceArray<GlobalObjectStore> shards = new AtomicReferenceArray<>(SHARD_COUNT);
    for (int shardIndex = 0; shardIndex < SHARD_COUNT; shardIndex++) {
      shards.set(shardIndex, new GlobalObjectStore(shardIndex));
    }
    return shards;
  }

  // the following fields represent a generation of each shard in the global object store

  private final int shardIndex;

  /** Supplies store keys where the key object is unused and eligible for collection. */
  private final ReferenceQueue<Object> staleKeys = new ReferenceQueue<>();

  /** Map of weak store keys to value objects. */
  private final ConcurrentHashMap<StoreKey, Object> map;

  /** Supplies old keys where the key object is unused and eligible for collection. */
  private final Supplier<Object> oldStaleKeys;

  /** Map of old store keys to value objects. */
  private final Map<StoreKey, Object> oldMap;

  private transient volatile int sampledYoungSize;

  private GlobalObjectStore(int shardIndex) {
    this.shardIndex = shardIndex;
    this.map = new ConcurrentHashMap<>();
    this.oldStaleKeys = NO_OLD_STALE_KEYS;
    this.oldMap = Collections.emptyMap();
  }

  private GlobalObjectStore(GlobalObjectStore oldStore) {
    this.shardIndex = oldStore.shardIndex;
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
    int estimatedSize = 0;
    for (int shardIndex = 0; shardIndex < SHARD_COUNT; shardIndex++) {
      estimatedSize += shards.get(shardIndex).doRemoveStaleEntries();
    }
    return estimatedSize;
  }

  private int doRemoveStaleEntries() {
    Object staleKey;

    // first remove stale entries from the young map
    while ((staleKey = staleKeys.poll()) != null) {
      //noinspection All: we know staleKey is a store key
      map.remove(staleKey);
    }

    // update young sample before we check old part of the store
    int estimatedTotal = map.size();
    sampledYoungSize = estimatedTotal;

    // next remove stale entries from the old map
    while ((staleKey = oldStaleKeys.get()) != null) {
      //noinspection All: we know staleKey is a store key
      oldMap.remove(staleKey);
    }

    estimatedTotal += oldMap.size();

    // randomly evict old content to keep us below the soft limit
    Iterator<StoreKey> itr = oldMap.keySet().iterator();
    while (estimatedTotal >= SHARD_SOFT_LIMIT && itr.hasNext()) {
      itr.next();
      itr.remove();
      estimatedTotal--;
    }

    return estimatedTotal;
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
      return shard(storeId).doGet(lookupKey);
    } finally {
      lookupKey.reset();
    }
  }

  @Nullable
  private Object doGet(LookupKey lookupKey) {
    //noinspection All: intentionally use lookup key without reference overhead
    Object value = map.get(lookupKey);
    //noinspection All: intentionally use lookup key without reference overhead
    return value != null ? value : oldMap.get(lookupKey);
  }

  /**
   * Associates the given key and store-id with the given value.
   *
   * @param key the key
   * @param storeId the store-id
   * @param value the new value
   */
  public static void put(Object key, int storeId, @Nullable Object value) {
    if (value != null) {
      shard(storeId).checkCapacity().doPut(key, storeId, value);
    } else {
      remove(key, storeId);
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
    LookupKey lookupKey = LookupKey.with(key, storeId);
    try {
      GlobalObjectStore s = shard(storeId);
      Object existing = s.doGet(lookupKey); // avoids creating unnecessary store key
      if (existing != null || value == null) {
        return existing;
      } else {
        return s.checkCapacity().doGetOrPut(key, storeId, value);
      }
    } finally {
      lookupKey.reset();
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
    LookupKey lookupKey = LookupKey.with(key, storeId);
    try {
      GlobalObjectStore s = shard(storeId);
      Object existing = s.doGet(lookupKey); // avoids creating unnecessary store key
      if (existing != null) {
        return existing;
      } else {
        return s.checkCapacity().doGetOrCompute(key, storeId, valueFunction);
      }
    } finally {
      lookupKey.reset();
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
    LookupKey lookupKey = LookupKey.with(key, storeId);
    try {
      return shard(storeId).doRemove(lookupKey);
    } finally {
      lookupKey.reset();
    }
  }

  @Nullable
  private Object doRemove(LookupKey lookupKey) {
    //noinspection All: intentionally use lookup key without reference overhead
    Object value = map.remove(lookupKey);
    //noinspection All: intentionally use lookup key without reference overhead
    Object oldValue = oldMap.remove(lookupKey);
    return value != null ? value : oldValue;
  }

  /**
   * Checks shard capacity, performing inline eviction or ageing if appropriate.
   *
   * @return the latest generation of the shard
   */
  private GlobalObjectStore checkCapacity() {
    int youngSize;
    if (ThreadLocalRandom.current().nextInt(SIZE_SAMPLE_RATE) == 0) {
      sampledYoungSize = youngSize = map.size();
    } else {
      youngSize = sampledYoungSize;
    }
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
   * Attempts to age this shard by one generation; if already ageing don't block, use latest.
   *
   * @return the latest generation of the shard
   */
  private GlobalObjectStore maybeAgeStore() {
    // first try to get the token that allows us to age this shard
    boolean attemptAgeing = ageing.compareAndSet(shardIndex, 0, 1);
    // only after this get the latest generation of the shard
    GlobalObjectStore s = shards.get(shardIndex);
    if (attemptAgeing) {
      try {
        if (s == this) {
          // our shard generation is still the latest; go ahead and age it
          shards.set(shardIndex, s = new GlobalObjectStore(this));
        }
      } finally {
        ageing.set(shardIndex, 0); // relinquish the token
      }
    }
    return s; // always return the latest generation of the shard
  }

  /** Returns the shard for the given store-id. */
  static GlobalObjectStore shard(int storeId) {
    // use fibonacci-hashing to spread store-ids evenly, then take top bits
    return shards.get((storeId * 0x9E3779B9) >>> (32 - SHARD_BITS));
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
