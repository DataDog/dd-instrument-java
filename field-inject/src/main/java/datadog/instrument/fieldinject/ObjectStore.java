/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.fieldinject;

import static datadog.instrument.fieldinject.ObjectStoreIds.objectStoreId;

import java.util.function.Function;
import javax.annotation.Nullable;

/**
 * Key-value store where keys and values are objects that implement or extend specific types.
 *
 * <p>When field-injection is enabled values may be stored directly inside keys; otherwise values
 * are recorded in a global weak map. Since the same object may participate in multiple stores each
 * global key captures the store identity along with a weak reference to the owning key object.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
@SuppressWarnings("unchecked")
public final class ObjectStore<K, V> {

  /**
   * Returns the {@link ObjectStore} for the given key-value type combination.
   *
   * @param keyType the key type
   * @param valueType the value type
   * @param <K> the key type
   * @param <V> the value type
   * @return the key-value's object-store
   */
  public static <K, V> ObjectStore<K, V> of(Class<K> keyType, Class<V> valueType) {
    return of(keyType.getName(), valueType.getName());
  }

  /**
   * Returns the {@link ObjectStore} for the given key-value type combination.
   *
   * @param keyType the key type
   * @param valueType the value type
   * @param <K> the key type
   * @param <V> the value type
   * @return the key-value's object-store
   */
  public static <K, V> ObjectStore<K, V> of(String keyType, String valueType) {
    return new ObjectStore<>(objectStoreId(keyType, valueType));
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
  private ObjectStore(int storeId) {
    this.storeId = storeId;
  }

  /**
   * Gets the value currently associated with the given key in this object-store.
   *
   * @param key the key
   * @return value associated with the key; {@code null} if there is no value
   */
  @Nullable
  public V get(K key) {
    return (V) GlobalObjectStore.get(key, storeId);
  }

  /**
   * Associates the given key with the given value in this object-store.
   *
   * @param key the key
   * @param value the new value
   */
  public void put(K key, V value) {
    GlobalObjectStore.put(key, storeId, value);
  }

  /**
   * Gets the value currently associated with the given key. If no value exists then associate the
   * key with the given value and return that.
   *
   * @param key the key
   * @param value the new value
   * @return existing value if present, otherwise the new value
   */
  public V getOrPut(K key, V value) {
    return (V) GlobalObjectStore.getOrPut(key, storeId, value);
  }

  /**
   * Gets the value currently associated with the given key. If no value exists then associate the
   * key with a value computed by the given function and return that.
   *
   * @param key the key
   * @param valueFunction the value function
   * @return existing value if present, otherwise the new computed value
   */
  public V getOrCompute(K key, Function<K, V> valueFunction) {
    return (V) GlobalObjectStore.getOrCompute(key, storeId, valueFunction);
  }

  /**
   * Removes the value associated with the given key in this object-store.
   *
   * @param key the key
   * @return value previously associated with the key; {@code null} if there was no value
   */
  @Nullable
  public V remove(K key) {
    return (V) GlobalObjectStore.remove(key, storeId);
  }
}
