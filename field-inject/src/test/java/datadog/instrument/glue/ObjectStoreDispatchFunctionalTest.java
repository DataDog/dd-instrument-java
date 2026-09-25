package datadog.instrument.glue;

import static datadog.instrument.fieldinject.ObjectStoreIds.objectStoreId;
import static org.assertj.core.api.Assertions.assertThat;

import datadog.instrument.fieldinject.GlobalObjectStore;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Loads and actually invokes the generated {@code ObjectStoreDispatch} bytecode, to check its
 * fast-path/fallback dispatch and double-checked locking behave correctly - not just that the
 * bytecode passes structural verification (see {@link ObjectStoreGlueGeneratorTest}).
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
  @SuppressFBWarnings("DP_CREATE_CLASSLOADER_INSIDE_DO_PRIVILEGED") // no security manager in tests
  static void defineGeneratedClasses() throws Exception {
    DynamicClassLoader loader =
        new DynamicClassLoader(ObjectStoreDispatchFunctionalTest.class.getClassLoader());

    keyWithValueClass =
        loader.define(
            "datadog/instrument/fieldinject/KeyWithValue",
            ObjectStoreGlueGenerator.generateKeyWithValueBytecode());
    Class<?> dispatchClass =
        loader.define(
            "datadog/instrument/fieldinject/ObjectStoreDispatch",
            ObjectStoreGlueGenerator.generateObjectStoreDispatchBytecode());

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

    putMethod.invoke(null, key, storeId, "value");

    assertThat(backing.get(storeId)).isEqualTo("value");
    assertThat(getMethod.invoke(null, key, storeId)).isEqualTo("value");
    assertThat(GlobalObjectStore.get(key, storeId))
        .as("fast path must bypass the global store entirely")
        .isNull();
  }

  @Test
  void fallbackPathDelegatesToGlobalObjectStoreForNonInjectedKey() throws Exception {
    int storeId = objectStoreId("test.Dispatch.Fallback.Key", "test.Dispatch.Fallback.Value");
    Object key = new Object();

    putMethod.invoke(null, key, storeId, "value");

    assertThat(GlobalObjectStore.get(key, storeId)).isEqualTo("value");
    assertThat(getMethod.invoke(null, key, storeId)).isEqualTo("value");
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
  void weakGetPutBypassAccessorAndUseGlobalStoreDirectly() throws Exception {
    int storeId = objectStoreId("test.Dispatch.Weak.Key", "test.Dispatch.Weak.Value");
    Map<Integer, Object> backing = Collections.synchronizedMap(new HashMap<>());
    Object key = fastPathKey(backing);

    weakPutMethod.invoke(null, key, storeId, "weak-value");

    assertThat(backing.get(storeId)).as("weakPut must not touch the fast-path accessor").isNull();
    assertThat(GlobalObjectStore.get(key, storeId)).isEqualTo("weak-value");
    assertThat(weakGetMethod.invoke(null, key, storeId)).isEqualTo("weak-value");
    assertThat(getMethod.invoke(null, key, storeId))
        .as("regular get must not see values stored via weakPut")
        .isNull();
  }

  @Test
  void concurrentGetOrPutOnSameKeyConvergesToASingleWinner() throws Exception {
    int storeId = objectStoreId("test.Dispatch.Concurrent.Key", "test.Dispatch.Concurrent.Value");
    Map<Integer, Object> backing = Collections.synchronizedMap(new HashMap<>());
    Object key = fastPathKey(backing);

    int threadCount = 8;
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

  /** Defines generated bytecode directly, so it can be loaded and invoked in-process. */
  private static final class DynamicClassLoader extends ClassLoader {
    DynamicClassLoader(ClassLoader parent) {
      super(parent);
    }

    Class<?> define(String internalName, byte[] bytecode) {
      return defineClass(internalName.replace('/', '.'), bytecode, 0, bytecode.length);
    }
  }
}
