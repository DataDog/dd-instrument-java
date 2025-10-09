/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classmatch;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Predicate;

/** Standard matchers, part of the public API. */
public final class StandardMatchers {

  private StandardMatchers() {}

  /**
   * Negates the given type matcher.
   *
   * @param matcher the matcher to negate
   * @return negation of the matcher
   */
  public static TypeMatcher not(TypeMatcher matcher) {
    return cs -> !matcher.test(cs);
  }

  /**
   * Negates the given class matcher.
   *
   * @param matcher the matcher to negate
   * @return negation of the matcher
   */
  public static ClassMatcher not(ClassMatcher matcher) {
    return c -> !matcher.test(c);
  }

  /**
   * Negates the given field matcher.
   *
   * @param matcher the matcher to negate
   * @return negation of the matcher
   */
  public static FieldMatcher not(FieldMatcher matcher) {
    return f -> !matcher.test(f);
  }

  /**
   * Negates the given method matcher.
   *
   * @param matcher the matcher to negate
   * @return negation of the matcher
   */
  public static MethodMatcher not(MethodMatcher matcher) {
    return m -> !matcher.test(m);
  }

  /**
   * Negates the given access matcher.
   *
   * @param matcher the matcher to negate
   * @return negation of the matcher
   */
  public static AccessMatcher not(AccessMatcher matcher) {
    return acc -> !matcher.test(acc);
  }

  /**
   * Syntactic sugar around {@link Predicate#negate()}.
   *
   * @param <T> the predicate's target type
   * @param predicate the predicate to negate
   * @return negated predicate
   */
  public static <T> Predicate<T> not(Predicate<T> predicate) {
    return predicate.negate();
  }

  /**
   * Matches when the name equals the given string.
   *
   * @param name the expected name
   * @return matcher of names with the same value
   */
  public static Predicate<String> named(String name) {
    return name::equals;
  }

  /**
   * Matches when the name equals one of the given strings.
   *
   * @param names the expected names
   * @return matcher of names from the given list
   */
  public static Predicate<String> namedOneOf(String... names) {
    return namedOneOf(asList(names));
  }

  /**
   * Matches when the name equals one of the given strings.
   *
   * @param names the expected names
   * @return matcher of names from the given list
   */
  public static Predicate<String> namedOneOf(Collection<String> names) {
    return new HashSet<>(names)::contains;
  }

  /**
   * Matches when the name starts with the given prefix.
   *
   * @param prefix the expected prefix
   * @return matcher of names starting with the prefix
   */
  public static Predicate<String> nameStartsWith(String prefix) {
    return s -> s.startsWith(prefix);
  }

  /**
   * Matches when the name ends with the given suffix.
   *
   * @param suffix the expected suffix
   * @return matcher of names ending with the suffix
   */
  public static Predicate<String> nameEndsWith(String suffix) {
    return s -> s.endsWith(suffix);
  }
}
