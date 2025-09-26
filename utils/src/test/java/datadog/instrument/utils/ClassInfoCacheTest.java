package datadog.instrument.utils;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ClassInfoCacheTest {

  @Test
  void basicOperation() {
    ClassInfoCache<String> cache = new ClassInfoCache<>(8 * 1024 * 1024);

    ClassLoader myCL = newCL();
    ClassLoader notMyCL = newCL();

    assertNull(cache.find("example.test.MyGlobalClass"));
    assertNull(cache.find("example.test.MyLocalClass"));
    assertNull(cache.find("example.test.NotMyClass"));

    assertNull(cache.find("example.test.MyGlobalClass", myCL));
    assertNull(cache.find("example.test.MyLocalClass", myCL));
    assertNull(cache.find("example.test.NotMyClass", myCL));

    assertNull(cache.find("example.test.MyGlobalClass", notMyCL));
    assertNull(cache.find("example.test.MyLocalClass", notMyCL));
    assertNull(cache.find("example.test.NotMyClass", notMyCL));

    cache.share("example.test.MyGlobalClass", "my global data");
    cache.share("example.test.MyLocalClass", "my local data", myCL);

    assertEquals("my global data", cache.find("example.test.MyGlobalClass"));
    assertEquals("my local data", cache.find("example.test.MyLocalClass"));
    assertNull(cache.find("example.test.NotMyClass"));

    assertEquals("my global data", cache.find("example.test.MyGlobalClass", myCL));
    assertEquals("my local data", cache.find("example.test.MyLocalClass", myCL));
    assertNull(cache.find("example.test.NotMyClass", myCL));

    assertEquals("my global data", cache.find("example.test.MyGlobalClass", notMyCL));
    assertNull(cache.find("example.test.MyLocalClass", notMyCL));
    assertNull(cache.find("example.test.NotMyClass", notMyCL));

    cache.clear();

    assertNull(cache.find("example.test.MyGlobalClass"));
    assertNull(cache.find("example.test.MyLocalClass"));
    assertNull(cache.find("example.test.NotMyClass"));

    assertNull(cache.find("example.test.MyGlobalClass", myCL));
    assertNull(cache.find("example.test.MyLocalClass", myCL));
    assertNull(cache.find("example.test.NotMyClass", myCL));

    assertNull(cache.find("example.test.MyGlobalClass", notMyCL));
    assertNull(cache.find("example.test.MyLocalClass", notMyCL));
    assertNull(cache.find("example.test.NotMyClass", notMyCL));
  }

  @Test
  @SuppressWarnings({
    "SimplifiableAssertion",
    "EqualsWithItself",
    "EqualsBetweenInconvertibleTypes",
    "ConstantValue"
  })
  void sharedInfoIndexedByClassName() {
    ClassInfoCache<String> cache = new ClassInfoCache<>(8);

    assertNull(cache.find("MyClass"));

    cache.share("MyClass", "first");
    assertEquals("first", cache.find("MyClass"));

    cache.share("MyClass", "second");
    assertEquals("second", cache.find("MyClass"));

    ClassInfoCache.SharedInfo infoA = new ClassInfoCache.SharedInfo("MyClass", "A", 0);
    ClassInfoCache.SharedInfo infoB = new ClassInfoCache.SharedInfo("MyClass", "B", 1);

    assertFalse(infoA.equals(null));
    assertFalse(infoA.equals(""));
    assertTrue(infoA.equals(infoA));
    assertTrue(infoA.equals(infoB));
    assertTrue(infoB.equals(infoA));

    assertEquals(infoA.hashCode(), infoB.hashCode());
  }

  @Test
  void overflow() {
    ClassInfoCache<Integer> cache = new ClassInfoCache<>(256);

    for (int i = 0; i < 300; i++) {
      cache.share("example.MyClass" + i, i);
    }

    assertNull(cache.find("example.MyClass"));

    Set<Integer> overwritten =
        new HashSet<>(
            asList(
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 13, 15, 17, 18, 20, 21, 22, 23, 24, 25, 26, 27, 28,
                31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 44, 46, 51, 53, 54, 57, 61, 62, 67, 70,
                71));

    for (int i = 0; i < 300; i++) {
      if (overwritten.contains(i)) {
        assertNull(cache.find("example.MyClass" + i), "rem " + i);
      } else {
        assertEquals(i, cache.find("example.MyClass" + i), "add " + i);
      }
    }
  }

  private static ClassLoader newCL() {
    return new ClassLoader() {};
  }
}
