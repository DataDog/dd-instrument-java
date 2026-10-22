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
    InternalNames names = new InternalNames(Collections.emptyList());
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
  void largeSet() {
    List<String> types = new ArrayList<>();
    for (int i = 0; i < 512; i++) {
      types.add("pkg.Type" + i);
    }
    checkNames(types);
  }

  @Test
  void collidingNamesFormProbeRun() {
    // The first four names collide in a 16-slot table.
    InternalNames names =
        checkNames(asList("pkg.Type0", "pkg.Type17", "pkg.Type28", "pkg.Type39", "例.型", ""));
    // These misses start at the same slot as the colliding names.
    assertMissing(names, "pkg/Type110", "pkg/Type121");
  }

  @Test
  void neighbouringProbeRunsMerge() {
    // Names starting in adjacent slots form a shared probe run.
    InternalNames names =
        checkNames(asList("pkg.Type0", "pkg.Type1", "pkg.Type17", "pkg.Type18", "例.型", ""));
    // The miss must probe past names from both starting slots.
    assertMissing(names, "pkg/Type29");
  }

  @Test
  void probeRunWrapsAround() {
    // Collisions at slot 15 wrap around a 16-slot table.
    InternalNames names =
        checkNames(asList("pkg.Type13", "pkg.Type24", "pkg.Type35", "pkg.Type14", "", "例.型"));
    // Misses must cross the end of the table too.
    assertMissing(names, "pkg/Type46", "pkg/Type25");
  }

  @Test
  void deduplicatesNormalizedNames() {
    checkNames(
        asList(
            "pkg/AaAa",
            "pkg.AaAa",
            "pkg.AaBB",
            "pkg.BBAa",
            "pkg.BBBB",
            "pkg.AaAa",
            "pkg.AaBB",
            "pkg.BBAa",
            "pkg.BBBB",
            "pkg/AaAa",
            "pkg.Type",
            "pkg.Type$Nested",
            "例.型",
            ""));
  }

  @Test
  void probeRunComparesPrefixesAndUnicode() {
    // Short names and Unicode characters share probe runs.
    InternalNames names = checkNames(asList("", "h", "hh", "hhh", "p", "\u0100"));
    assertMissing(names, "hhhh", "x", "\u0100h", "\ufff8");
  }

  @Test
  void manyIdenticalHashesAndMissingNames() {
    List<String> types = new ArrayList<>();
    for (int i = 0; i < 256; i += 2) {
      types.add(collidingName(i));
    }
    InternalNames names = checkNames(types);
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

  @Test
  void setContract() {
    InternalNames names = new InternalNames(asList("pkg.Type", "pkg.Other"));
    Iterator<String> iterator = names.iterator();
    Set<String> actual = new HashSet<>();
    while (iterator.hasNext()) {
      assertTrue(iterator.hasNext());
      assertTrue(actual.add(iterator.next()));
    }
    assertEquals(new HashSet<>(asList("pkg/Type", "pkg/Other")), actual);
    assertThrows(UnsupportedOperationException.class, iterator::remove);
    assertThrows(NoSuchElementException.class, iterator::next);
    assertFalse(names.contains(null));
    assertFalse(names.contains(123));
  }

  private static InternalNames checkNames(List<String> types) {
    InternalNames names = new InternalNames(types);
    Set<String> expected = new HashSet<>();
    for (String type : types) {
      String name = InternalMatchers.internalName(type);
      expected.add(name);
      assertTrue(names.containsType(name), name);
      assertTrue(names.contains(query(name)), name);
      assertTrue(names.containsType(typeString(name)), name);
    }
    assertEquals(expected.size(), names.size());
    assertEquals(expected, new HashSet<>(names));
    assertMissing(names, "!", "pkg/", "pkg/Type$", "pkg/Type$NestedExtra", "例/型別", "\uffff");
    return names;
  }

  private static void assertMissing(InternalNames names, String... missing) {
    for (String name : missing) {
      assertFalse(names.containsType(name), name);
      assertFalse(names.containsType(query(name)), name);
      assertFalse(names.containsType(typeString(name)), name);
    }
  }

  private static String collidingName(int bits) {
    StringBuilder name = new StringBuilder("pkg/");
    for (int i = 0; i < 8; i++) {
      name.append((bits & (1 << i)) == 0 ? "Aa" : "BB");
    }
    return name.toString();
  }

  private static TypeString typeString(String name) {
    String descriptor = "L" + name + ";";
    return new TypeString(descriptor, 1, descriptor.length() - 1, name.hashCode());
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
