package datadog.instrument.fieldinject;

import static datadog.instrument.fieldinject.GlobalObjectStore.SHARD_COUNT;
import static datadog.instrument.fieldinject.ObjectStoreIds.objectStoreId;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.IdentityHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Checks that store-ids spread evenly across shards for common usage patterns.
 *
 * <p>Store-ids are built from key/value type-ids handed out sequentially, so a shard formula that
 * just masks the low bits can badly cluster or even collapse onto a single shard; these tests guard
 * against that regressing. Type names are unique per test to avoid interference from other tests
 * sharing the same type-id cache.
 */
class ObjectStoreShardingTest {

  private static void assertSpreadAcrossShards(int[] storeIds) {
    Map<GlobalObjectStore, Integer> countsByShard = new IdentityHashMap<>();
    for (int storeId : storeIds) {
      GlobalObjectStore shard = GlobalObjectStore.shard(storeId);
      countsByShard.merge(shard, 1, Integer::sum);
    }

    int used = countsByShard.size();
    int max = countsByShard.values().stream().mapToInt(Integer::intValue).max().orElse(0);

    assertTrue(
        used >= SHARD_COUNT / 2,
        "expected at least half the shards to be used, got " + used + "/" + SHARD_COUNT);

    int average = storeIds.length / SHARD_COUNT;
    assertTrue(
        max <= average * 3,
        "expected no shard to receive far more than its share, got max="
            + max
            + " for average="
            + average);
  }

  @Test
  void distinctKeyAndValueTypesPerStoreSpreadAcrossShards() {
    int n = 64;
    int[] storeIds = new int[n];
    for (int i = 0; i < n; i++) {
      storeIds[i] = objectStoreId("DistinctKey" + i, "DistinctValue" + i);
    }
    assertSpreadAcrossShards(storeIds);
  }

  @Test
  void sharedKeyTypeWithManyValueTypesSpreadsAcrossShards() {
    int n = 64;
    int[] storeIds = new int[n];
    for (int i = 0; i < n; i++) {
      storeIds[i] = objectStoreId("SharedKeyType", "InjectedFieldType" + i);
    }
    assertSpreadAcrossShards(storeIds);
  }

  @Test
  void smallKeyTypePoolWithGrowingValueTypePoolSpreadsAcrossShards() {
    String[] keyTypes = {
      "PoolSpan", "PoolAgentSpan", "PoolHttpRequest", "PoolHttpResponse", "PoolDBStatement"
    };
    int n = 60;
    int[] storeIds = new int[n];
    for (int i = 0; i < n; i++) {
      storeIds[i] = objectStoreId(keyTypes[i % keyTypes.length], "PoolInjectedField" + i);
    }
    assertSpreadAcrossShards(storeIds);
  }
}
