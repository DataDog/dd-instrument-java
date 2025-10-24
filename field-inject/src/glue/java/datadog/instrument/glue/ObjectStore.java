/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.glue;

import java.util.function.Function;

/**
 * Key-value store where keys and values are objects that implement or extend specific types.
 *
 * <p>When field-injection is enabled values may be stored directly inside keys; otherwise values
 * are recorded in a global weak map. Since the same object may participate in multiple stores the
 * global key captures the store identity along with a weak reference to the original key.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class ObjectStore {

  /** Keys that store their associated values in bytecode-injected fields. */
  public interface InjectedKey {

    /** Retrieves value from the injected field backing the given store. */
    Object $get$__datadogObjectStore$(int storeId);

    /** Stores given value in the injected field backing the given store. */
    void $put$__datadogObjectStore$(int storeId, Object value);
  }

  /**
   * Removes stale entries from the global object-store, where the key is now unused.
   *
   * <p>It is the caller's responsibility to decide how often to call {@code #removeStaleEntries}.
   * It may be periodically with a background thread, on certain requests, or some other condition.
   */
  public static void removeStaleEntries() {
    GlobalObjectStore.removeStaleEntries();
  }

  // used to disambiguate requests for different stores on the same key instance
  private final int storeId;

  /**
   * @param storeId the internally allocated store id
   */
  public ObjectStore(int storeId) {
    this.storeId = storeId;
  }

  /**
   * Gets the value currently associated with the given key in this object-store.
   *
   * @param key the key
   * @return value associated with the key; {@code null} if there is no value
   */
  public Object get(Object key) {
    if (key instanceof InjectedKey) {
      return ((InjectedKey) key).$get$__datadogObjectStore$(storeId);
    } else {
      return GlobalObjectStore.get(key, storeId);
    }
  }

  /**
   * Associates the given key with the given value in this object-store.
   *
   * @param key the key
   * @param value the new value
   */
  public void put(Object key, Object value) {
    if (key instanceof InjectedKey) {
      ((InjectedKey) key).$put$__datadogObjectStore$(storeId, value);
    } else {
      GlobalObjectStore.put(key, storeId, value);
    }
  }

  /**
   * If the given key is not already associated with a value in this object-store, associates it
   * with the given value. Unlike {@link java.util.Map#putIfAbsent} this always returns the final
   * associated value.
   *
   * @param key the key
   * @param value the new value
   * @return old value if it was present, otherwise the new value
   */
  public Object putIfAbsent(Object key, Object value) {
    if (key instanceof InjectedKey) {
      InjectedKey access = (InjectedKey) key;
      Object existing = access.$get$__datadogObjectStore$(storeId);
      if (existing == null) {
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (access) {
          existing = access.$get$__datadogObjectStore$(storeId);
          if (existing == null) {
            access.$put$__datadogObjectStore$(storeId, (existing = value));
          }
        }
      }
      return existing;
    } else {
      return GlobalObjectStore.putIfAbsent(key, storeId, value);
    }
  }

  /**
   * If the given key is not already associated with a value in this object-store, associates it
   * with a value computed by the given function.
   *
   * @param key the key
   * @param valueFunction the value function
   * @return old value if it was present, otherwise the new computed value
   */
  public Object computeIfAbsent(Object key, Function valueFunction) {
    if (key instanceof InjectedKey) {
      InjectedKey access = (InjectedKey) key;
      Object existing = access.$get$__datadogObjectStore$(storeId);
      if (existing == null) {
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (access) {
          existing = access.$get$__datadogObjectStore$(storeId);
          if (existing == null) {
            access.$put$__datadogObjectStore$(storeId, (existing = valueFunction.apply(key)));
          }
        }
      }
      return existing;
    } else {
      return GlobalObjectStore.computeIfAbsent(key, storeId, valueFunction);
    }
  }

  /**
   * Removes the value associated with the given key in this object-store.
   *
   * @param key the key
   * @return value previously associated with the key; {@code null} if there was no value
   */
  public Object remove(Object key) {
    if (key instanceof InjectedKey) {
      InjectedKey access = (InjectedKey) key;
      Object existing = access.$get$__datadogObjectStore$(storeId);
      if (existing != null) {
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (access) {
          existing = access.$get$__datadogObjectStore$(storeId);
          if (existing != null) {
            access.$put$__datadogObjectStore$(storeId, null);
          }
        }
      }
      return existing;
    } else {
      return GlobalObjectStore.remove(key, storeId);
    }
  }
}
