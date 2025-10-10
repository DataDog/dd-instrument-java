/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classmatch;

import static datadog.instrument.classmatch.InternalMatchers.internalName;
import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.function.Predicate;

/** Fluent-API for building type hierarchy predicates. */
@FunctionalInterface
public interface TypeMatcher extends Predicate<CharSequence> {

  /**
   * Matches when the type equals the given string.
   *
   * @param type the expected type
   * @return matcher of types with the same value
   */
  static TypeMatcher type(String type) {
    return internalName(type)::contentEquals;
  }

  /**
   * Matches when the type equals one of the given strings.
   *
   * @param types the expected types
   * @return matcher of types from the given list
   */
  static TypeMatcher typeOneOf(String... types) {
    return typeOneOf(asList(types));
  }

  /**
   * Matches when the type equals one of the given strings.
   *
   * @param types the expected types
   * @return matcher of types from the given list
   */
  static TypeMatcher typeOneOf(Collection<String> types) {
    return new InternalNames(types)::containsType;
  }

  /**
   * Matches when the type starts with the given prefix.
   *
   * @param prefix the expected prefix
   * @return matcher of types starting with the prefix
   */
  static TypeMatcher typeStartsWith(String prefix) {
    String internalPrefix = internalName(prefix);
    return cs -> TypeString.startsWith(cs, internalPrefix);
  }

  /**
   * Matches when the type ends with the given suffix.
   *
   * @param suffix the expected suffix
   * @return matcher of types ending with the suffix
   */
  static TypeMatcher typeEndsWith(String suffix) {
    String internalSuffix = internalName(suffix);
    return cs -> TypeString.endsWith(cs, internalSuffix);
  }

  /**
   * Conjunction of this matcher AND another.
   *
   * @param other the other matcher
   * @return conjunction of both matchers
   */
  default TypeMatcher and(TypeMatcher other) {
    return new InternalMatchers.TypeConjunction(this, other);
  }

  /**
   * Disjunction of this matcher OR another.
   *
   * @param other the other matcher
   * @return disjunction of both matchers
   */
  default TypeMatcher or(TypeMatcher other) {
    return new InternalMatchers.TypeDisjunction(this, other);
  }
}
