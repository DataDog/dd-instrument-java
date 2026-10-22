package datadog.instrument.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.tabletest.junit.TableTest;

@SuppressFBWarnings("DM_GC")
@SuppressWarnings({"UnusedAssignment", "ConstantValue"})
class ClassLoaderValueTest {

  private final AtomicInteger count = new AtomicInteger();

  private final ClassLoaderValue<String> classLoaderValue =
      new ClassLoaderValue<String>() {
        @Override
        protected String computeValue(ClassLoader cl) {
          return count.getAndIncrement() + "=" + cl;
        }
      };

  @Test
  void basicOperation() {
    ClassLoader cl0 = null;
    ClassLoader cl1 = ClassLoader.getSystemClassLoader();
    ClassLoader cl2 = newCL("CL2");
    ClassLoader cl3 = newCL("CL3");
    ClassLoader cl4 = newCL("CL4");
    ClassLoader cl5 = newCL("CL5");

    assertEquals(0, classLoaderValue.size());

    assertThat(classLoaderValue.get(cl0)).isEqualTo("0=null");
    assertThat(classLoaderValue.get(cl1)).matches("1=.*App.*");
    assertThat(classLoaderValue.get(cl2)).isEqualTo("2=CL2");
    assertThat(classLoaderValue.get(cl3)).isEqualTo("3=CL3");
    assertThat(classLoaderValue.get(cl4)).isEqualTo("4=CL4");
    assertThat(classLoaderValue.get(cl5)).isEqualTo("5=CL5");

    assertEquals(6, classLoaderValue.size());

    // repeated requests should return the same values
    assertThat(classLoaderValue.get(cl5)).isEqualTo("5=CL5");
    assertThat(classLoaderValue.get(cl4)).isEqualTo("4=CL4");
    assertThat(classLoaderValue.get(cl3)).isEqualTo("3=CL3");
    assertThat(classLoaderValue.get(cl2)).isEqualTo("2=CL2");
    assertThat(classLoaderValue.get(cl1)).matches("1=.*App.*");
    assertThat(classLoaderValue.get(cl0)).isEqualTo("0=null");

    assertEquals(6, classLoaderValue.size());

    classLoaderValue.remove(cl0);

    assertEquals(5, classLoaderValue.size());

    // only the removed value should be replaced
    assertThat(classLoaderValue.get(cl0)).isEqualTo("6=null");
    assertThat(classLoaderValue.get(cl1)).matches("1=.*App.*");
    assertThat(classLoaderValue.get(cl2)).isEqualTo("2=CL2");
    assertThat(classLoaderValue.get(cl3)).isEqualTo("3=CL3");
    assertThat(classLoaderValue.get(cl4)).isEqualTo("4=CL4");
    assertThat(classLoaderValue.get(cl5)).isEqualTo("5=CL5");

    assertEquals(6, classLoaderValue.size());

    classLoaderValue.remove(cl1);

    assertEquals(5, classLoaderValue.size());

    // only the removed value should be replaced
    assertThat(classLoaderValue.get(cl0)).isEqualTo("6=null");
    assertThat(classLoaderValue.get(cl1)).matches("7=.*App.*");
    assertThat(classLoaderValue.get(cl2)).isEqualTo("2=CL2");
    assertThat(classLoaderValue.get(cl3)).isEqualTo("3=CL3");
    assertThat(classLoaderValue.get(cl4)).isEqualTo("4=CL4");
    assertThat(classLoaderValue.get(cl5)).isEqualTo("5=CL5");

    assertEquals(6, classLoaderValue.size());

    classLoaderValue.remove(cl3);

    assertEquals(5, classLoaderValue.size());

    // only the removed value should be replaced
    assertThat(classLoaderValue.get(cl0)).isEqualTo("6=null");
    assertThat(classLoaderValue.get(cl1)).matches("7=.*App.*");
    assertThat(classLoaderValue.get(cl2)).isEqualTo("2=CL2");
    assertThat(classLoaderValue.get(cl3)).isEqualTo("8=CL3");
    assertThat(classLoaderValue.get(cl4)).isEqualTo("4=CL4");
    assertThat(classLoaderValue.get(cl5)).isEqualTo("5=CL5");

    assertEquals(6, classLoaderValue.size());

    classLoaderValue.clear();

    assertEquals(0, classLoaderValue.size());
  }

  @Test
  @Timeout(30)
  void removeStaleEntries() {
    Map<ClassLoader, String> visited = new HashMap<>();

    ClassLoader cl0 = null;
    ClassLoader cl1 = ClassLoader.getSystemClassLoader();
    ClassLoader cl2 = newCL("CL2");
    ClassLoader cl3 = newCL("CL3");
    ClassLoader cl4 = newCL("CL4");
    ClassLoader cl5 = newCL("CL5");

    assertEquals(0, classLoaderValue.size());

    classLoaderValue.visit(visited::put);
    assertEquals(0, visited.size());
    visited.clear();

    assertThat(classLoaderValue.get(cl0)).isEqualTo("0=null");
    assertThat(classLoaderValue.get(cl1)).matches("1=.*App.*");
    assertThat(classLoaderValue.get(cl2)).isEqualTo("2=CL2");
    assertThat(classLoaderValue.get(cl3)).isEqualTo("3=CL3");
    assertThat(classLoaderValue.get(cl4)).isEqualTo("4=CL4");
    assertThat(classLoaderValue.get(cl5)).isEqualTo("5=CL5");

    assertEquals(6, classLoaderValue.size());

    classLoaderValue.visit(visited::put);
    assertThat(visited.values())
        .containsExactlyInAnyOrder(
            classLoaderValue.get(cl0),
            classLoaderValue.get(cl1),
            classLoaderValue.get(cl2),
            classLoaderValue.get(cl3),
            classLoaderValue.get(cl4),
            classLoaderValue.get(cl5));
    visited.clear();

    cl2 = null;
    cl4 = null;

    while (classLoaderValue.size() > 4) {
      System.gc();
      System.runFinalization();
      ClassLoaderValue.removeStaleEntries();
    }

    classLoaderValue.visit(visited::put);
    assertThat(visited.values())
        .containsExactlyInAnyOrder(
            classLoaderValue.get(cl0),
            classLoaderValue.get(cl1),
            classLoaderValue.get(cl3),
            classLoaderValue.get(cl5));
    visited.clear();
  }

  @TableTest({
    "systemLoader",
    "false       ",
    "true        "
  })
  void competingComputationsReturnTheInstalledValue(boolean systemLoader) throws Exception {
    ClassLoader loader = systemLoader ? ClassLoader.getSystemClassLoader() : null;
    int threadCount = 8;
    CountDownLatch computing = new CountDownLatch(threadCount);
    AtomicInteger computeCalls = new AtomicInteger();
    ClassLoaderValue<Object> values =
        new ClassLoaderValue<Object>() {
          @Override
          protected Object computeValue(ClassLoader cl) {
            assertThat(cl).isSameAs(loader);
            computeCalls.incrementAndGet();
            // Nobody can install a value until every contender is already computing one.
            computing.countDown();
            try {
              assertThat(computing.await(10, TimeUnit.SECONDS)).isTrue();
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
              throw new AssertionError(e);
            }
            return new Object();
          }
        };

    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    List<Future<Object>> results = new ArrayList<>();
    try {
      for (int i = 0; i < threadCount; i++) {
        results.add(executor.submit(() -> values.get(loader)));
      }
      Object winner = results.get(0).get(10, TimeUnit.SECONDS);
      assertThat(winner).isNotNull();
      for (Future<Object> result : results) {
        assertThat(result.get(10, TimeUnit.SECONDS)).isSameAs(winner);
      }
      assertThat(values.get(loader)).isSameAs(winner);
      assertThat(computeCalls).hasValue(threadCount);
      assertThat(values.size()).isEqualTo(1);
    } finally {
      executor.shutdownNow();
    }
  }

  private static ClassLoader newCL(String name) {
    return new ClassLoader() {
      @Override
      public String toString() {
        return name;
      }
    };
  }
}
