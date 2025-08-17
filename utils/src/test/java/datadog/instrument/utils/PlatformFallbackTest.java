package datadog.instrument.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.JRE;

class PlatformFallbackTest {
  static String javaVersion;

  @BeforeAll
  static void setUp() {
    javaVersion = System.getProperty("java.version");
    System.setProperty("java.version", "unparseable");
  }

  @AfterAll
  static void tearDown() {
    System.setProperty("java.version", javaVersion);
  }

  @Test
  void atLeastJava() {
    assertTrue(Platform.atLeastJava(JRE.currentVersionNumber()));
  }
}
