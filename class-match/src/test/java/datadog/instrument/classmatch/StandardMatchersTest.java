package datadog.instrument.classmatch;

import static datadog.instrument.classmatch.StandardMatchers.nameEndsWith;
import static datadog.instrument.classmatch.StandardMatchers.nameStartsWith;
import static datadog.instrument.classmatch.StandardMatchers.named;
import static datadog.instrument.classmatch.StandardMatchers.namedOneOf;
import static datadog.instrument.classmatch.StandardMatchers.not;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;

class StandardMatchersTest {
  @Test
  void exactNamesAreCaseSensitiveAndDoNotNormalize() {
    assertTrue(named("sample.Type").test("sample.Type"));
    assertFalse(named("sample.Type").test("sample/Type"));
    assertFalse(named("sample.Type").test("Sample.Type"));
    assertFalse(named("sample.Type").test(null));
    assertTrue(named("").test(""));
  }

  @Test
  void membershipOverloadsAndSnapshot() {
    List<String> names = new ArrayList<>(Arrays.asList("one", "two", "one"));
    Predicate<String> matcher = namedOneOf(names);
    names.clear();
    assertTrue(matcher.test("one"));
    assertTrue(matcher.test("two"));
    assertFalse(matcher.test("three"));
    assertTrue(namedOneOf("one", "two").test("two"));
    assertFalse(namedOneOf().test(""));
    assertFalse(namedOneOf(names).test("one"));
  }

  @Test
  void prefixSuffixAndGenericNegation() {
    Predicate<String> matcher = nameStartsWith("get").and(nameEndsWith("Value"));
    assertTrue(matcher.test("getValue"));
    assertFalse(matcher.test("setValue"));
    assertFalse(matcher.test("getValues"));
    assertTrue(nameStartsWith("").test(""));
    assertTrue(nameEndsWith("").test(""));
    assertFalse(nameStartsWith("longer").test("long"));
    assertFalse(nameEndsWith("longer").test("long"));
    assertTrue(not(matcher).test("setValue"));
    assertFalse(not(matcher).test("getValue"));
  }
}
