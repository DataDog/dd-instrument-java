package datadog.instrument.fieldinject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages unique {@link ObjectStore} ids for each key-value type combination.
 *
 * <p>Only used when field-injection is not supported or enabled.
 */
final class ObjectStoreIds {

  private static final Map<String, Short> typeIds = new ConcurrentHashMap<>();
  private static final AtomicInteger nextTypeId = new AtomicInteger();

  /**
   * Returns the {@link ObjectStore} id for the given key-value type combination.
   *
   * @param keyType the key type
   * @param valueType the value type
   * @return the key-value's store id
   */
  static int objectStoreId(String keyType, String valueType) {
    return typeId(keyType) << 16 | typeId(valueType);
  }

  private static short typeId(String type) {
    return typeIds.computeIfAbsent(type, unused -> (short) nextTypeId.getAndIncrement());
  }
}
