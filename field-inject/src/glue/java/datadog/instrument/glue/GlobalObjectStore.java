/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.glue;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class GlobalObjectStore {

  private static final Map<WeakKey, Object> weakMap = new ConcurrentHashMap<>();

  private GlobalObjectStore() {}

  public static void cleanStaleKeys() {
    Reference<?> ref;
    while ((ref = WeakKey.staleKeys.poll()) != null) {
      //noinspection SuspiciousMethodCalls
      weakMap.remove(ref);
    }
  }

  public static Object get(Object key, int storeId) {
    //noinspection SuspiciousMethodCalls
    return weakMap.get(new LookupKey(key, storeId));
  }

  public static void put(Object key, int storeId, Object value) {
    if (value != null) {
      weakMap.put(new WeakKey(key, storeId), value);
    } else {
      //noinspection SuspiciousMethodCalls
      weakMap.remove(new LookupKey(key, storeId));
    }
  }

  public static Object putIfAbsent(Object key, int storeId, Object value) {
    Object existing = weakMap.putIfAbsent(new WeakKey(key, storeId), value);
    return existing != null ? existing : value;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static Object computeIfAbsent(Object key, int storeId, Function factory) {
    return weakMap.computeIfAbsent(new WeakKey(key, storeId), factory);
  }

  public static Object remove(Object key, int storeId) {
    //noinspection SuspiciousMethodCalls
    return weakMap.remove(new LookupKey(key, storeId));
  }

  /** Key used to weakly associate a non-injected key and store-id with a value. */
  private static final class WeakKey extends WeakReference<Object> {

    // stale store keys that are now eligible for collection
    static final ReferenceQueue<Object> staleKeys = new ReferenceQueue<>();

    final int hash;
    final int storeId;

    WeakKey(Object key, int storeId) {
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
      } else if (o instanceof WeakKey) {
        WeakKey storeKey = (WeakKey) o;
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
      if (o instanceof WeakKey) {
        WeakKey storeKey = (WeakKey) o;
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
