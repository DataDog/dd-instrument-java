package datadog.instrument.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ClassLoaderKeyTest {

  @Test
  @SuppressWarnings({
    "SimplifiableAssertion",
    "EqualsWithItself",
    "EqualsBetweenInconvertibleTypes",
    "ConstantValue"
  })
  void equals() {
    ClassLoader cl1 = ClassLoader.getSystemClassLoader();
    ClassLoader cl2 = newCL();

    ClassLoaderKey clKey1 = new ClassLoaderKey(cl1, System.identityHashCode(cl1));
    ClassLoaderKey clKey2 = new ClassLoaderKey(cl2, System.identityHashCode(cl2));

    ClassLoaderKey.LookupKey lookupKey1 = new ClassLoaderKey.LookupKey(cl1);
    ClassLoaderKey.LookupKey lookupKey2 = new ClassLoaderKey.LookupKey(cl2);

    assertTrue(clKey1.equals(clKey1));
    assertTrue(clKey1.equals(lookupKey1));
    assertTrue(lookupKey1.equals(clKey1));
    assertTrue(lookupKey1.equals(lookupKey1));

    assertTrue(clKey2.equals(clKey2));
    assertTrue(clKey2.equals(lookupKey2));
    assertTrue(lookupKey2.equals(clKey2));
    assertTrue(lookupKey2.equals(lookupKey2));

    assertFalse(clKey1.equals(null));
    assertFalse(clKey1.equals(""));
    assertFalse(clKey1.equals(clKey2));
    assertFalse(clKey1.equals(lookupKey2));
    assertFalse(clKey2.equals(null));
    assertFalse(clKey2.equals(""));
    assertFalse(clKey2.equals(clKey1));
    assertFalse(clKey2.equals(lookupKey1));
    assertFalse(lookupKey1.equals(null));
    assertFalse(lookupKey1.equals(""));
    assertFalse(lookupKey1.equals(clKey2));
    assertFalse(lookupKey1.equals(lookupKey2));
    assertFalse(lookupKey2.equals(null));
    assertFalse(lookupKey2.equals(""));
    assertFalse(lookupKey2.equals(clKey1));
    assertFalse(lookupKey2.equals(lookupKey1));
  }

  private static ClassLoader newCL() {
    return new ClassLoader() {
      // empty; only used to check class-loader identity
    };
  }
}
