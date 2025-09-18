/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classmatch;

import static datadog.instrument.classmatch.InternalMatchers.descriptor;

import java.util.function.IntPredicate;
import java.util.function.Predicate;

/** Fluent-API for building {@link FieldOutline} predicates. */
@FunctionalInterface
public interface FieldMatcher extends Predicate<FieldOutline> {

  /**
   * Matches fields with the given name.
   *
   * @param name the expected name
   * @return matcher of fields that have the same name
   */
  static FieldMatcher field(String name) {
    return f -> name.equals(f.fieldName);
  }

  /**
   * Matches fields with names matching the given criteria.
   *
   * @param nameMatcher the name matcher
   * @return matcher of fields whose name matches tbe criteria
   */
  static FieldMatcher field(Predicate<String> nameMatcher) {
    return f -> nameMatcher.test(f.fieldName);
  }

  /**
   * Matches fields with access flags matching the given criteria.
   *
   * @param accessMatcher the access flag matcher
   * @return matcher of fields whose access flags match tbe criteria
   */
  default FieldMatcher withAccess(IntPredicate accessMatcher) {
    return and(f -> accessMatcher.test(f.access));
  }

  /**
   * Matches fields of the given type.
   *
   * @param type the type name
   * @return matcher of fields of the same type
   */
  default FieldMatcher ofType(String type) {
    String descriptor = descriptor(type);
    return and(f -> descriptor.equals(f.descriptor));
  }

  /**
   * Matches fields of the given type.
   *
   * @param type the type name
   * @return matcher of fields of the same type
   */
  default FieldMatcher ofType(Class<?> type) {
    String descriptor = descriptor(type);
    return and(f -> descriptor.equals(f.descriptor));
  }

  /**
   * Conjunction of this matcher AND another.
   *
   * @param other the other matcher
   * @return conjunction of both matchers
   */
  default FieldMatcher and(FieldMatcher other) {
    // simple approach as we don't expect many field-matcher unions
    return f -> test(f) && other.test(f);
  }

  /**
   * Disjunction of this matcher OR another.
   *
   * @param other the other matcher
   * @return disjunction of both matchers
   */
  default FieldMatcher or(FieldMatcher other) {
    // simple approach as we don't expect many field-matcher unions
    return f -> test(f) || other.test(f);
  }
}
