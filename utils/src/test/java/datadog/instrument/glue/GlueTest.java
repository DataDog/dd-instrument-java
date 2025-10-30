package datadog.instrument.glue;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.MissingResourceException;
import org.junit.jupiter.api.Test;

class GlueTest {

  @Test
  void glueResource() {

    final byte[] expectedBytecode = {
      -54, -2, -70, -66, 0, 0, 0, 52, 0, 10, 10, 0, 2, 0, 3, 7, 0, 4, 12, 0, 5, 0, 6, 1, 0, 16, 106,
      97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 1, 0, 6, 60, 105, 110, 105,
      116, 62, 1, 0, 3, 40, 41, 86, 7, 0, 8, 1, 0, 7, 69, 120, 97, 109, 112, 108, 101, 1, 0, 4, 67,
      111, 100, 101, 0, 32, 0, 7, 0, 2, 0, 0, 0, 0, 0, 1, 0, 0, 0, 5, 0, 6, 0, 1, 0, 9, 0, 0, 0, 17,
      0, 1, 0, 1, 0, 0, 0, 5, 42, -73, 0, 1, -79, 0, 0, 0, 0, 0, 0
    };

    assertArrayEquals(expectedBytecode, Glue.loadBytecode(GlueTest.class, "test.glue"));
  }

  @Test
  void missingGlueResource() {
    assertThrows(
        MissingResourceException.class, () -> Glue.loadBytecode(GlueTest.class, "missing.glue"));
  }
}
