package datadog.instrument.classmatch;

import static datadog.instrument.classmatch.BytecodeTestSupport.annotatedClass;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.tabletest.junit.TableTest;

class AnnotationRegistrationTest {
  @TempDir Path temporary;

  @TableTest({
    "mode        ",
    "'single'    ",
    "'collection'"
  })
  void firstRegistrationWorksInAFreshJvm(String mode) throws Exception {
    Path output = temporary.resolve("probe-" + mode + ".log");
    Process process =
        new ProcessBuilder(
                Paths.get(System.getProperty("java.home"), "bin", "java").toString(),
                "-cp",
                System.getProperty("java.class.path"),
                AnnotationRegistrationProbe.class.getName(),
                mode)
            .redirectErrorStream(true)
            .redirectOutput(output.toFile())
            .start();
    try {
      assertTrue(process.waitFor(20, TimeUnit.SECONDS), "annotation probe timed out");
      assertEquals(
          0, process.exitValue(), new String(Files.readAllBytes(output), StandardCharsets.UTF_8));
    } finally {
      process.destroyForcibly();
      process.waitFor(5, TimeUnit.SECONDS);
    }
  }

  @Test
  void concurrentRegistrationPublishesCompleteSnapshotsWithoutLosingNames() throws Exception {
    int writers = 4, readers = 2, registrations = 12;
    List<String> names = new ArrayList<>();
    for (int i = 0; i < writers * registrations; i++) {
      names.add("tests/Concurrent/Marker" + i);
    }
    byte[] bytes = annotatedClass(names.toArray(new String[0]));
    Set<String> allowed = new HashSet<>(names);
    CountDownLatch start = new CountDownLatch(1);
    ExecutorService pool = Executors.newFixedThreadPool(writers + readers);
    List<Future<?>> tasks = new ArrayList<>();
    try {
      for (int worker = 0; worker < writers + readers; worker++) {
        final int id = worker;
        tasks.add(
            pool.submit(
                () -> {
                  start.await();
                  Set<String> registered = new HashSet<>();
                  for (int i = 0; i < registrations; i++) {
                    if (id < writers) {
                      String name = names.get(id * registrations + i);
                      if ((id & 1) == 0) {
                        ClassFile.annotationOfInterest(name);
                      } else {
                        ClassFile.annotationsOfInterest(Arrays.asList(name, name));
                      }
                      registered.add(name);
                    }
                    assertSnapshot(bytes, registered, allowed);
                  }
                  return null;
                }));
      }
      start.countDown();
      for (Future<?> task : tasks) {
        task.get(30, TimeUnit.SECONDS);
      }
      assertSnapshot(bytes, allowed, allowed);
    } finally {
      pool.shutdownNow();
      assertTrue(pool.awaitTermination(15, TimeUnit.SECONDS), "annotation workers did not stop");
    }
  }

  private static void assertSnapshot(byte[] bytes, Set<String> required, Set<String> allowed) {
    ClassOutline outline = ClassFile.outline(bytes);
    for (String[] annotations :
        new String[][] {outline.annotations, outline.methods[0].annotations}) {
      Set<String> actual = new HashSet<>(Arrays.asList(annotations));
      assertEquals(annotations.length, actual.size(), "snapshot contains duplicate names");
      assertTrue(actual.containsAll(required), "snapshot lost registered names");
      assertTrue(allowed.containsAll(actual), "snapshot contains unexpected names");
    }
  }
}
