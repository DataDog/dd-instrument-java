/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.fieldinject;

import static datadog.instrument.fieldinject.ObjectStoreIds.objectStoreId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class ObjectStoreIdsTest {

  @Test
  void sameTypePairReturnsSameId() {
    int id1 = objectStoreId("com.example.Key", "com.example.Value");
    int id2 = objectStoreId("com.example.Key", "com.example.Value");
    assertEquals(id1, id2);
  }

  @Test
  void differentKeyTypesDifferentId() {
    int idA = objectStoreId("com.example.KeyA", "com.example.SharedValue");
    int idB = objectStoreId("com.example.KeyB", "com.example.SharedValue");
    assertNotEquals(idA, idB);
  }

  @Test
  void differentValueTypesDifferentId() {
    int idA = objectStoreId("com.example.SharedKey", "com.example.ValueA");
    int idB = objectStoreId("com.example.SharedKey", "com.example.ValueB");
    assertNotEquals(idA, idB);
  }

  @Test
  void swappedTypePairDifferentId() {
    int idAB = objectStoreId("com.example.TypeX", "com.example.TypeY");
    int idBA = objectStoreId("com.example.TypeY", "com.example.TypeX");
    assertNotEquals(idAB, idBA);
  }

  @Test
  void keyTypeEncodedInLowerBits() {
    // same key type => same lower 16 bits, regardless of value type
    int id1 = objectStoreId("com.example.StableKey", "com.example.Val1");
    int id2 = objectStoreId("com.example.StableKey", "com.example.Val2");
    assertEquals(id1 & 0xFFFF, id2 & 0xFFFF);
  }

  @Test
  void valueTypeEncodedInUpperBits() {
    // same value type => same upper 16 bits, regardless of key type
    int id1 = objectStoreId("com.example.Key1", "com.example.StableValue");
    int id2 = objectStoreId("com.example.Key2", "com.example.StableValue");
    assertEquals((id1 >> 16) & 0xFFFF, (id2 >> 16) & 0xFFFF);
  }

  @Test
  void distinctPairsHaveDistinctIds() {
    String[] types = {
      "com.example.Alpha", "com.example.Beta", "com.example.Gamma", "com.example.Delta",
    };
    Set<Integer> ids = new HashSet<>();
    for (String keyType : types) {
      for (String valueType : types) {
        ids.add(objectStoreId(keyType, valueType));
      }
    }
    assertEquals(types.length * types.length, ids.size());
  }

  @Test
  void concurrentAccessReturnsSameId() throws Exception {
    int threads = 8;
    CountDownLatch ready = new CountDownLatch(threads);
    CountDownLatch start = new CountDownLatch(1);
    ExecutorService executor = Executors.newFixedThreadPool(threads);
    try {
      @SuppressWarnings("unchecked")
      Future<Integer>[] futures = new Future[threads];
      for (int i = 0; i < threads; i++) {
        futures[i] =
            executor.submit(
                () -> {
                  ready.countDown();
                  start.await();
                  return objectStoreId("com.example.ConcurrentKey", "com.example.ConcurrentValue");
                });
      }
      ready.await();
      start.countDown();
      int expected = futures[0].get(5, TimeUnit.SECONDS);
      for (Future<Integer> future : futures) {
        assertEquals(expected, future.get(5, TimeUnit.SECONDS));
      }
    } finally {
      executor.shutdown();
    }
  }
}
