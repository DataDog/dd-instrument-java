/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.glue;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Global key-value store used when field-injection is not possible. Since the same object may
 * participate in multiple stores each global key captures the store identity along with a weak
 * reference to the original key.
 */
public final class GlobalObjectStore {

  private static final Map<StoreKey, Object> weakMap = new ConcurrentHashMap<>();

  private GlobalObjectStore() {}

  /**
   * Removes stale entries from the global object-store, where the key is now unused.
   *
   * <p>It is the caller's responsibility to decide how often to call {@code #removeStaleEntries}.
   * It may be periodically with a background thread, on certain requests, or some other condition.
   */
  public static void removeStaleEntries() {
    StoreKey key;
    while ((key = StoreKey.poll()) != null) {
      weakMap.remove(key);
    }
  }

  /**
   * Gets the value currently associated with the given key and store-id.
   *
   * @param key the key
   * @param storeId the store-id
   * @return value associated with the key; {@code null} if there is no value
   */
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
    if (value != null) {
      weakMap.put(new StoreKey(key, storeId), value);
    } else {
      //noinspection All: intentionally use lookup key without reference overhead
      weakMap.remove(new LookupKey(key, storeId));
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
    Object existing = weakMap.putIfAbsent(new StoreKey(key, storeId), value);
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
    return weakMap.computeIfAbsent(new StoreKey(key, storeId), valueFunction);
  }

  /**
   * Removes the value associated with the given key and store-id.
   *
   * @param key the key
   * @param storeId the store-id
   * @return value previously associated with the key; {@code null} if there was no value
   */
  public static Object remove(Object key, int storeId) {
    //noinspection All: intentionally use lookup key without reference overhead
    return weakMap.remove(new LookupKey(key, storeId));
  }

  /** Key used to weakly associate a non-injected key and store-id with a value. */
  private static final class StoreKey extends WeakReference<Object> {

    // stale store keys that are now eligible for collection
    private static final ReferenceQueue<Object> staleKeys = new ReferenceQueue<>();

    final int hash;
    final int storeId;

    StoreKey(Object key, int storeId) {
      super(key, staleKeys);
      this.hash = (31 * storeId) + System.identityHashCode(key);
      this.storeId = storeId;
    }

    static StoreKey poll() {
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
