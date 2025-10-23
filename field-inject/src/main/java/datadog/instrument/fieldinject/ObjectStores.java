package datadog.instrument.fieldinject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("rawtypes")
final class ObjectStores {
  private static final Map<String, ObjectStore> stores = new ConcurrentHashMap<>();
  private static final AtomicInteger nextStoreId = new AtomicInteger();

  static ObjectStore objectStore(String keyClass, String valueClass) {
    return stores.computeIfAbsent(
        keyClass + ';' + valueClass, unused -> new ObjectStore(nextStoreId.getAndIncrement()));
  }
}
