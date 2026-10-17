/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classmatch;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;

class InternalNamesTest {
  private static final List<String> COLLISIONS =
      asList("pkg.AaAa", "pkg.AaBB", "pkg.BBAa", "pkg.BBBB");

  @Test
  void empty() {
    InternalNames names = InternalNames.create(Collections.emptyList());
    assertEquals(0, names.size());
    assertFalse(names.containsType(""));
    assertFalse(names.iterator().hasNext());
    assertThrows(NoSuchElementException.class, () -> names.iterator().next());
  }

  @Test
  void normalNames() {
    checkNames(
        asList("java.lang.String", "java/lang/String", "pkg.Type", "pkg.Type$Nested", "例.型", ""));
  }

  @Test
  void largeNamesRetainHashedLookups() {
    List<String> types = new ArrayList<>();
    Set<Integer> slots = new HashSet<>();
    // 512 names start with a 1024-slot table. Choose distinct slots to avoid resizing.
    for (int i = 0; types.size() < 512; i++) {
      String name = "pkg/Type" + i;
      if (slots.add(name.hashCode() & 1023)) {
        types.add(name);
      }
    }
    InternalNames names = InternalNames.create(types);
    assertEquals("Hashed", names.getClass().getSimpleName());
    checkNames(types);
  }

  @Test
  void identicalHashes() {
    for (String type : COLLISIONS) {
      assertEquals(COLLISIONS.get(0).hashCode(), type.hashCode());
    }
    checkNames(COLLISIONS);
  }

  @Test
  void fallbackDeduplicatesNormalizedNames() {
    List<String> types = new ArrayList<>(COLLISIONS);
    types.addAll(COLLISIONS);
    types.addAll(asList("pkg/AaAa", "pkg.Type", "pkg.Type$Nested", "例.型", ""));
    checkNames(types);
  }

  @Test
  void fallbackHandlesManyCollisionsAndMissingNames() {
    List<String> types = new ArrayList<>();
    for (int i = 0; i < 256; i += 2) {
      types.add(collidingName(i));
    }
    InternalNames names = InternalNames.create(types);
    assertEquals(types.size(), names.size());
    for (int i = 0; i < 256; i++) {
      String name = collidingName(i);
      assertEquals(i % 2 == 0, names.containsType(query(name)));
    }
  }

  @Test
  void matcherFactoriesSupportCollisions() {
    TypeMatcher matcher = TypeMatcher.typeOneOf(COLLISIONS);
    for (String type : COLLISIONS) {
      assertTrue(matcher.test(query(InternalMatchers.internalName(type))));
    }
    assertFalse(matcher.test(query("pkg/AaAaAa")));
    Predicate<String[]> annotations = InternalMatchers.declaresAnnotationOneOf(COLLISIONS);
    assertTrue(annotations.test(new String[] {"pkg/Unknown", "pkg/BBBB"}));
    assertFalse(annotations.test(new String[] {"pkg/Unknown"}));
  }

  private static void checkNames(List<String> types) {
    InternalNames names = InternalNames.create(types);
    Set<String> expected = new HashSet<>();
    for (String type : types) {
      String name = InternalMatchers.internalName(type);
      expected.add(name);
      assertTrue(names.containsType(name));
      assertTrue(names.contains(query(name)));
      String descriptor = "L" + name + ";";
      assertTrue(
          names.containsType(
              new TypeString(descriptor, 1, descriptor.length() - 1, name.hashCode())));
    }
    assertEquals(expected.size(), names.size());
    Set<String> actual = new HashSet<>();
    Iterator<String> iterator = names.iterator();
    while (iterator.hasNext()) {
      assertTrue(actual.add(iterator.next()));
    }
    assertThrows(UnsupportedOperationException.class, iterator::remove);
    assertEquals(expected, actual);
    assertThrows(NoSuchElementException.class, iterator::next);
    assertFalse(names.contains(null));
    assertFalse(names.contains(123));
    for (String missing :
        asList("!", "pkg/", "pkg/Type$", "pkg/Type$NestedExtra", "例/型別", "\uffff")) {
      assertFalse(names.containsType(query(missing)));
    }
  }

  private static String collidingName(int bits) {
    StringBuilder name = new StringBuilder("pkg/");
    for (int i = 0; i < 8; i++) {
      name.append((bits & (1 << i)) == 0 ? "Aa" : "BB");
    }
    return name.toString();
  }

  /** String-compatible hash, identity equality, and no conversion to String allowed. */
  private static CharSequence query(String value) {
    return new CharSequence() {
      @Override
      public int length() {
        return value.length();
      }

      @Override
      public char charAt(int index) {
        return value.charAt(index);
      }

      @Override
      public CharSequence subSequence(int start, int end) {
        throw new UnsupportedOperationException();
      }

      @Override
      public int hashCode() {
        return value.hashCode();
      }

      @Override
      public String toString() {
        throw new AssertionError("Lookup must not allocate a String");
      }
    };
  }
}
