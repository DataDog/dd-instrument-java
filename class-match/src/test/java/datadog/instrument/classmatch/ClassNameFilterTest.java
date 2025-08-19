package datadog.instrument.classmatch;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ClassNameFilterTest {

  @Test
  void basicOperation() {
    ClassNameFilter filter = new ClassNameFilter(8 * 1024 * 1024);

    assertFalse(filter.contains("example.test.MyClass"));
    assertFalse(filter.contains("example.test.NotMyClass"));

    filter.add("example.test.MyClass");

    assertTrue(filter.contains("example.test.MyClass"));
    assertFalse(filter.contains("example.test.NotMyClass"));

    filter.clear();

    assertFalse(filter.contains("example.test.MyClass"));
    assertFalse(filter.contains("example.test.NotMyClass"));
  }

  @Test
  void uniqueClassCodes() {
    ClassNameFilter filter = new ClassNameFilter(8);

    assertEquals("example.test.FB".hashCode(), "example.test.Ea".hashCode());

    assertFalse(filter.contains("example.test.FB"));
    assertFalse(filter.contains("example.test.Ea"));

    filter.add("example.test.FB");

    assertTrue(filter.contains("example.test.FB"));
    assertFalse(filter.contains("example.test.Ea"));

    filter.add("example.test.Ea");

    assertTrue(filter.contains("example.test.FB"));
    assertFalse(filter.contains("example.test.Ea"));
  }

  @Test
  void overflow() {
    ClassNameFilter filter = new ClassNameFilter(256);

    for (int i = 0; i < 300; i++) {
      filter.add("example.MyClass" + i);
    }

    assertFalse(filter.contains("example.MyClass"));

    Set<Integer> overwritten =
        new HashSet<>(
            asList(
                0, 3, 4, 21, 22, 23, 24, 27, 31, 32, 33, 34, 36, 37, 38, 40, 44, 45, 50, 51, 53, 55,
                57, 58, 61, 62, 63, 64, 66, 67, 68, 79, 87, 89, 102, 104, 105, 113, 116, 118, 141,
                156, 193, 261));

    for (int i = 0; i < 300; i++) {
      if (overwritten.contains(i)) {
        assertFalse(filter.contains("example.MyClass" + i));
      } else {
        assertTrue(filter.contains("example.MyClass" + i));
      }
    }
  }
}
