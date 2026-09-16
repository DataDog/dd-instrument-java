package datadog.instrument.fieldinject;

import static datadog.instrument.fieldinject.ObjectStoreIds.objectStoreId;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Loads and actually invokes the generated {@code ObjectStoreDispatch} bytecode, to check its
 * fast-path/fallback dispatch and double-checked locking behave correctly - not just that the
 * bytecode passes structural verification (see {@code ObjectStoreGlueGeneratorTest}).
 *
 * <p>The generated classes are loaded from the build-time glue on the test classpath, so they share
 * the same class loader (and runtime package) as the package-private {@link GlobalObjectStore}.
 */
class ObjectStoreDispatchFunctionalTest {

  private static Class<?> keyWithValueClass;
  private static Method getMethod;
  private static Method putMethod;
  private static Method getOrPutMethod;
  private static Method getOrComputeMethod;
  private static Method removeMethod;
  private static Method weakGetMethod;
  private static Method weakPutMethod;

  @BeforeAll
  static void loadGeneratedClasses() throws Exception {
    keyWithValueClass = Class.forName("datadog.instrument.fieldinject.KeyWithValue");
    Class<?> dispatchClass = Class.forName("datadog.instrument.fieldinject.ObjectStoreDispatch");

    getMethod = dispatchClass.getMethod("get", Object.class, int.class);
    putMethod = dispatchClass.getMethod("put", Object.class, int.class, Object.class);
    getOrPutMethod = dispatchClass.getMethod("getOrPut", Object.class, int.class, Object.class);
    getOrComputeMethod =
        dispatchClass.getMethod("getOrCompute", Object.class, int.class, Function.class);
    removeMethod = dispatchClass.getMethod("remove", Object.class, int.class);
    weakGetMethod = dispatchClass.getMethod("weakGet", Object.class, int.class);
    weakPutMethod = dispatchClass.getMethod("weakPut", Object.class, int.class, Object.class);
  }

  @Test
  void fastPathRoundTripsThroughAccessorAndBypassesGlobalStore() throws Exception {
    int storeId = objectStoreId("test.Dispatch.FastPath.Key", "test.Dispatch.FastPath.Value");
    Map<Integer, Object> backing = Collections.synchronizedMap(new HashMap<>());
    Object key = fastPathKey(backing);

    // seed the global store, so we can tell if the fast path ever reads from or writes to it
    GlobalObjectStore.put(key, storeId, "global");

    assertThat(getMethod.invoke(null, key, storeId))
        .as("fast path get must not fall back to the global store")
        .isNull();

    putMethod.invoke(null, key, storeId, "value");

    assertThat(backing.get(storeId)).isEqualTo("value");
    assertThat(getMethod.invoke(null, key, storeId)).isEqualTo("value");
    assertThat(GlobalObjectStore.get(key, storeId))
        .as("fast path put must not write to the global store")
        .isEqualTo("global");
  }

  @Test
  void fallbackPathDelegatesAllOperationsToGlobalObjectStoreForNonInjectedKey() throws Exception {
    int storeId = objectStoreId("test.Dispatch.Fallback.Key", "test.Dispatch.Fallback.Value");
    Object key = new Object();

    putMethod.invoke(null, key, storeId, "value");

    assertThat(GlobalObjectStore.get(key, storeId)).isEqualTo("value");
    assertThat(getMethod.invoke(null, key, storeId)).isEqualTo("value");
    assertThat(getOrPutMethod.invoke(null, key, storeId, "ignored")).isEqualTo("value");
    assertThat(removeMethod.invoke(null, key, storeId)).isEqualTo("value");
    assertThat(GlobalObjectStore.get(key, storeId)).isNull();

    assertThat(getOrPutMethod.invoke(null, key, storeId, "put-if-absent"))
        .isEqualTo("put-if-absent");
    assertThat(GlobalObjectStore.get(key, storeId)).isEqualTo("put-if-absent");
    assertThat(removeMethod.invoke(null, key, storeId)).isEqualTo("put-if-absent");

    AtomicInteger computeCalls = new AtomicInteger();
    Function<Object, Object> compute =
        k -> {
          computeCalls.incrementAndGet();
          return "computed";
        };
    assertThat(getOrComputeMethod.invoke(null, key, storeId, compute)).isEqualTo("computed");
    assertThat(getOrComputeMethod.invoke(null, key, storeId, compute)).isEqualTo("computed");
    assertThat(computeCalls).hasValue(1);
    assertThat(GlobalObjectStore.get(key, storeId)).isEqualTo("computed");
  }

  @Test
  void getOrPutOnlySetsAccessorWhenAbsent() throws Exception {
    int storeId = objectStoreId("test.Dispatch.GetOrPut.Key", "test.Dispatch.GetOrPut.Value");
    Map<Integer, Object> backing = Collections.synchronizedMap(new HashMap<>());
    Object key = fastPathKey(backing);

    assertThat(getOrPutMethod.invoke(null, key, storeId, "first")).isEqualTo("first");
    assertThat(backing.get(storeId)).isEqualTo("first");

    assertThat(getOrPutMethod.invoke(null, key, storeId, "second")).isEqualTo("first");
    assertThat(backing.get(storeId))
        .as("existing accessor value must not be overwritten")
        .isEqualTo("first");
  }

  @Test
  void getOrComputeInvokesFunctionWithOriginalKeyOnlyWhenAbsent() throws Exception {
    int storeId =
        objectStoreId("test.Dispatch.GetOrCompute.Key", "test.Dispatch.GetOrCompute.Value");
    Map<Integer, Object> backing = Collections.synchronizedMap(new HashMap<>());
    Object key = fastPathKey(backing);
    AtomicReference<Object> receivedKey = new AtomicReference<>();

    Function<Object, Object> computeOnce =
        k -> {
          receivedKey.set(k);
          return "computed";
        };
    assertThat(getOrComputeMethod.invoke(null, key, storeId, computeOnce)).isEqualTo("computed");
    assertThat(receivedKey.get())
        .as("function should receive the original key, not the accessor")
        .isSameAs(key);
    assertThat(backing.get(storeId)).isEqualTo("computed");

    AtomicBoolean called = new AtomicBoolean(false);
    Function<Object, Object> shouldNotRun =
        k -> {
          called.set(true);
          return "should-not-be-used";
        };
    assertThat(getOrComputeMethod.invoke(null, key, storeId, shouldNotRun)).isEqualTo("computed");
    assertThat(called).as("compute function must not run when a value already exists").isFalse();
  }

  @Test
  void removeReturnsPreviousValueAndClearsAccessor() throws Exception {
    int storeId = objectStoreId("test.Dispatch.Remove.Key", "test.Dispatch.Remove.Value");
    Map<Integer, Object> backing = Collections.synchronizedMap(new HashMap<>());
    Object key = fastPathKey(backing);
    backing.put(storeId, "existing");

    assertThat(removeMethod.invoke(null, key, storeId)).isEqualTo("existing");
    assertThat(backing.get(storeId)).isNull();
  }

  @Test
  void removeDoesNotWriteToAccessorWhenValueIsAbsent() throws Exception {
    int storeId =
        objectStoreId("test.Dispatch.RemoveAbsent.Key", "test.Dispatch.RemoveAbsent.Value");
    AtomicInteger setterCalls = new AtomicInteger();
    InvocationHandler handler =
        (proxy, method, args) -> {
          if (args.length == 1) {
            return null;
          }
          setterCalls.incrementAndGet();
          return null;
        };
    Object key =
        Proxy.newProxyInstance(
            keyWithValueClass.getClassLoader(), new Class<?>[] {keyWithValueClass}, handler);

    assertThat(removeMethod.invoke(null, key, storeId)).isNull();
    assertThat(setterCalls).hasValue(0);
  }

  @Test
  void injectedAccessorFallsBackViaWeakGetPutForStoresItDoesNotHave() throws Exception {
    int injectedId = objectStoreId("test.Dispatch.Weak.Key", "test.Dispatch.Weak.Injected");
    int fallbackId = objectStoreId("test.Dispatch.Weak.Key", "test.Dispatch.Weak.Fallback");
    Map<Integer, Object> backing = Collections.synchronizedMap(new HashMap<>());
    Object key = injectedKey(backing, Collections.singleton(injectedId));

    putMethod.invoke(null, key, fallbackId, "fallback");
    assertThat(getMethod.invoke(null, key, fallbackId)).isEqualTo("fallback");
    assertThat(GlobalObjectStore.get(key, fallbackId)).isEqualTo("fallback");

    assertThat(getOrPutMethod.invoke(null, key, fallbackId, "ignored")).isEqualTo("fallback");
    assertThat(removeMethod.invoke(null, key, fallbackId)).isEqualTo("fallback");
    assertThat(GlobalObjectStore.get(key, fallbackId))
        .as("remove must clear the global store via weakPut")
        .isNull();
    assertThat(
            getOrComputeMethod.invoke(
                null, key, fallbackId, (Function<Object, Object>) k -> "computed"))
        .isEqualTo("computed");
    assertThat(GlobalObjectStore.get(key, fallbackId)).isEqualTo("computed");

    // stores the accessor does have should still use the injected field
    putMethod.invoke(null, key, injectedId, "injected");
    assertThat(backing).containsOnlyKeys(injectedId);
    assertThat(GlobalObjectStore.get(key, injectedId)).isNull();
  }

  @Test
  void concurrentGetOrPutOnSameKeyConvergesToASingleWinner() throws Exception {
    int storeId = objectStoreId("test.Dispatch.Concurrent.Key", "test.Dispatch.Concurrent.Value");
    Map<Integer, Object> backing = Collections.synchronizedMap(new HashMap<>());
    int threadCount = 8;
    CountDownLatch initialReads = new CountDownLatch(threadCount);
    AtomicInteger getterCalls = new AtomicInteger();
    AtomicInteger setterCalls = new AtomicInteger();
    InvocationHandler handler =
        (proxy, method, args) -> {
          if (args.length == 1) {
            if (getterCalls.incrementAndGet() <= threadCount) {
              initialReads.countDown();
              if (!initialReads.await(10, TimeUnit.SECONDS)) {
                throw new AssertionError("not every contender reached the initial accessor read");
              }
            }
            return backing.get((Integer) args[0]);
          }
          setterCalls.incrementAndGet();
          backing.put((Integer) args[0], args[1]);
          return null;
        };
    Object key =
        Proxy.newProxyInstance(
            keyWithValueClass.getClassLoader(), new Class<?>[] {keyWithValueClass}, handler);

    CountDownLatch start = new CountDownLatch(1);
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    List<Object> results = new ArrayList<>();
    try {
      List<java.util.concurrent.Future<Object>> futures = new ArrayList<>();
      for (int t = 0; t < threadCount; t++) {
        String candidate = "candidate-" + t;
        futures.add(
            executor.submit(
                () -> {
                  start.await();
                  return getOrPutMethod.invoke(null, key, storeId, candidate);
                }));
      }
      start.countDown();
      for (java.util.concurrent.Future<Object> future : futures) {
        results.add(future.get(10, TimeUnit.SECONDS));
      }
    } finally {
      executor.shutdownNow();
    }

    Object winner = backing.get(storeId);
    assertThat(setterCalls)
        .as("only the first contender inside the lock should set the value")
        .hasValue(1);
    assertThat(results)
        .as("double-checked locking should make every caller see the same winning value")
        .allMatch(winner::equals);
  }

  /**
   * Creates a {@code KeyWithValue} proxy backed by a plain map, simulating a field-injected key.
   */
  private static Object fastPathKey(Map<Integer, Object> backing) {
    InvocationHandler handler =
        (proxy, method, args) -> {
          if (args.length == 1) {
            return backing.get((Integer) args[0]);
          }
          backing.put((Integer) args[0], args[1]);
          return null;
        };
    return Proxy.newProxyInstance(
        keyWithValueClass.getClassLoader(), new Class<?>[] {keyWithValueClass}, handler);
  }

  /**
   * Mimics a field-injected key: stores it has fields for use the backing map, anything else falls
   * back via {@code ObjectStoreDispatch.weakGet/weakPut} (injected code can't assume it has access
   * to {@code GlobalObjectStore}). Fails fast if the accessor is re-entered by a recursive
   * fallback.
   */
  private static Object injectedKey(Map<Integer, Object> backing, Set<Integer> injectedIds) {
    AtomicInteger depth = new AtomicInteger();
    InvocationHandler handler =
        (proxy, method, args) -> {
          if (depth.incrementAndGet() > 1) {
            // fail fast, rather than recursing until the stack overflows
            throw new AssertionError("weakGet/weakPut must not re-enter the accessor");
          }
          try {
            Integer storeId = (Integer) args[0];
            if (args.length == 1) {
              return injectedIds.contains(storeId)
                  ? backing.get(storeId)
                  : weakGetMethod.invoke(null, proxy, storeId);
            }
            if (injectedIds.contains(storeId)) {
              backing.put(storeId, args[1]);
            } else {
              weakPutMethod.invoke(null, proxy, storeId, args[1]);
            }
            return null;
          } finally {
            depth.decrementAndGet();
          }
        };
    return Proxy.newProxyInstance(
        keyWithValueClass.getClassLoader(), new Class<?>[] {keyWithValueClass}, handler);
  }
}
