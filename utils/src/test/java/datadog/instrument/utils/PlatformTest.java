package datadog.instrument.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PlatformTest {

  @Test
  @SuppressWarnings("Since15")
  void atLeastJava() {
    int javaVersion;
    try {
      javaVersion = Runtime.version().feature();
    } catch (Throwable e) {
      javaVersion = 8;
    }

    assertTrue(Platform.atLeastJava(javaVersion - 1));
    assertTrue(Platform.atLeastJava(javaVersion));
    assertFalse(Platform.atLeastJava(javaVersion + 1));
  }
}
