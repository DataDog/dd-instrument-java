/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classmatch;

import java.util.function.Predicate;

/** Fluent-API for building type hierarchy predicates. */
public interface TypeMatcher extends Predicate<CharSequence> {

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
