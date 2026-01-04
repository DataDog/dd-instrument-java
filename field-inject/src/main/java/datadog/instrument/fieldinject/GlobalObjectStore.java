/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.fieldinject;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import javax.annotation.Nullable;

/**
 * Global key-value store used when field-injection is not possible. Since the same object may
 * participate in multiple stores each global key captures the store identity along with a weak
 * reference to the original key.
 */
public final class GlobalObjectStore {

  /** Never allow more than this number of objects in the global store. */
  private static final int GLOBAL_HARD_LIMIT = 100_000;

  /** Temporarily allow more than this number of objects, but start removing old content. */
  private static final int GLOBAL_SOFT_LIMIT = 50_000;

  /** Threshold at which we start doing limited cleanup at the same time as put operations. */
  private static final int INLINE_CLEANUP_THRESHOLD = 1024;

  /** Threshold at which we start sampling keys to track old content. */
  private static final int OLD_KEYS_THRESHOLD = 512;

  private static final Map<StoreKey, Object> weakMap = new ConcurrentHashMap<>();

  private static final Set<StoreKey> oldKeys = new HashSet<>();

  private static int previousEstimate = 0;

  private GlobalObjectStore() {}

  /**
   * Removes stale entries from the global object-store, where the key object is now unused.
   *
   * <p>It is the caller's responsibility to decide how often to call {@code #removeStaleEntries}.
   * It may be periodically with a background thread, on certain requests, or some other condition.
   */
  public static synchronized void removeStaleEntries() {

    Map<StoreKey, Object> weakMap = GlobalObjectStore.weakMap;
    int estimatedSize = weakMap.size(); // capture size before any cleanup
    StoreKey key;
    while ((key = StoreKey.pollStaleKeys()) != null) {
      weakMap.remove(key);
      estimatedSize--;
    }

    // perform additional work only after a period of growth/reduction
    if (Math.abs(estimatedSize - previousEstimate) >= OLD_KEYS_THRESHOLD
        || estimatedSize >= (GLOBAL_HARD_LIMIT + GLOBAL_SOFT_LIMIT) / 2) {

      if (estimatedSize >= GLOBAL_SOFT_LIMIT) {
        // start proactively removing old content to keep growth in check
        for (StoreKey oldKey : oldKeys) {
          if (weakMap.remove(oldKey) != null) {
            estimatedSize--;
          }
        }
        oldKeys.clear();
      } else {
        // have any of the old previously sampled keys been collected?
        oldKeys.removeIf(StoreKey::isStale);
      }

      int refill = OLD_KEYS_THRESHOLD - oldKeys.size();
      if (refill > 0) {
        // sample of keys at this time, don't need strict age ordering
        weakMap.keySet().stream().limit(refill).forEach(oldKeys::add);
      }

      previousEstimate = estimatedSize;
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
    //noinspection All: intentionally use lookup key without reference overhead
    return weakMap.get(new LookupKey(key, storeId));
  }

  /**
   * Associates the given key and store-id with the given value.
   *
   * @param key the key
   * @param storeId the store-id
   * @param value the new value
   */
  public static void put(Object key, int storeId, Object value) {
    if (value == null) {
      //noinspection All: intentionally use lookup key without reference overhead
      weakMap.remove(new LookupKey(key, storeId));
    } else if (checkCapacity()) {
      weakMap.put(new StoreKey(key, storeId), value);
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
  public static Object getOrPut(Object key, int storeId, Object value) {
    //noinspection All: intentionally use lookup key without reference overhead
    Object existing = weakMap.get(new LookupKey(key, storeId));
    if (existing == null && value != null && checkCapacity()) {
      existing = weakMap.putIfAbsent(new StoreKey(key, storeId), value);
    }
    return existing != null ? existing : value;
  }

  /**
   * Gets the value currently associated with the given key and store-id. If no value exists then
   * associate the key and store-id with a value computed by the given function and return that.
   *
   * @param key the key
   * @param storeId the store-id
   * @param valueFunction the value function
   * @return existing value if present, otherwise the new computed value
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static Object getOrCompute(Object key, int storeId, Function valueFunction) {
    //noinspection All: intentionally use lookup key without reference overhead
    Object existing = weakMap.get(new LookupKey(key, storeId));
    if (existing == null && checkCapacity()) {
      existing =
          weakMap.computeIfAbsent(
              new StoreKey(key, storeId), storeKey -> valueFunction.apply(storeKey.get()));
    }
    return existing != null ? existing : valueFunction.apply(key);
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
    //noinspection All: intentionally use lookup key without reference overhead
    return weakMap.remove(new LookupKey(key, storeId));
  }

  /**
   * @return {@code true} if there is space to add new objects.
   */
  private static boolean checkCapacity() {
    int estimatedSize = weakMap.size();
    if (estimatedSize >= INLINE_CLEANUP_THRESHOLD) {
      // periodic cleanup may not be enough, start performing inline cleanup
      StoreKey staleKey = StoreKey.pollStaleKeys();
      if (staleKey == null) {
        return estimatedSize < GLOBAL_HARD_LIMIT;
      }
      weakMap.remove(staleKey);
    }
    return true;
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

    boolean isStale() {
      return get() == null;
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
    final Object key;
    final int hash;
    final int storeId;

    LookupKey(Object key, int storeId) {
      this.key = key;
      this.hash = (31 * storeId) + System.identityHashCode(key);
      this.storeId = storeId;
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
