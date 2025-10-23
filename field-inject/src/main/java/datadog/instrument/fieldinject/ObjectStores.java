package datadog.instrument.fieldinject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages unique {@link ObjectStore}s for each key-value type combination.
 *
 * <p>Only used when field-injection is not supported or enabled.
 */
@SuppressWarnings("rawtypes")
final class ObjectStores {

  // no need for weak collections here; each 'store' just contains a primitive id
  private static final Map<String, ObjectStore> stores = new ConcurrentHashMap<>();
  private static final AtomicInteger nextStoreId = new AtomicInteger();

  /**
   * Returns the {@link ObjectStore} for the given key-value type combination.
   *
   * @param keyType the key type
   * @param valueType the value type
   * @return the key-value's object-store
   */
  static ObjectStore objectStore(String keyType, String valueType) {
    return stores.computeIfAbsent(
        keyType + ';' + valueType, unused -> new ObjectStore(nextStoreId.getAndIncrement()));
  }
}
