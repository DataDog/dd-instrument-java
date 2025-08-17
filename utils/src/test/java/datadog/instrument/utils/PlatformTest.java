package datadog.instrument.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.JRE;

class PlatformTest {

  @Test
  void atLeastJava() {
    assertTrue(Platform.atLeastJava(JRE.currentVersionNumber() - 1));
    assertTrue(Platform.atLeastJava(JRE.currentVersionNumber()));
    assertFalse(Platform.atLeastJava(JRE.currentVersionNumber() + 1));
  }
}
