package datadog.instrument.fieldinject;

import static datadog.instrument.fieldinject.ObjectStores.objectStore;

import datadog.instrument.glue.GlobalObjectStore;
import java.util.function.Function;

/**
 *
 *
 * @param <K> the key type
 * @param <V> the value type
 */
@SuppressWarnings("unchecked")
public final class ObjectStore<K, V> {

  public static <K, V> ObjectStore<K, V> of(Class<K> keyClass, Class<V> valueClass) {
    return of(keyClass.getName(), valueClass.getName());
  }

  public static <K, V> ObjectStore<K, V> of(String keyClass, String valueClass) {
    return objectStore(keyClass, valueClass);
  }

  private final int storeId;

  ObjectStore(int storeId) {
    this.storeId = storeId;
  }

  public V get(K key) {
    return (V) GlobalObjectStore.get(key, storeId);
  }

  public void put(K key, V value) {
    GlobalObjectStore.put(key, storeId, value);
  }

  public V putIfAbsent(K key, V value) {
    return (V) GlobalObjectStore.putIfAbsent(key, storeId, value);
  }

  public V computeIfAbsent(K key, Function<K, V> factory) {
    return (V) GlobalObjectStore.computeIfAbsent(key, storeId, factory);
  }

  public V remove(K key) {
    return (V) GlobalObjectStore.remove(key, storeId);
  }
}
