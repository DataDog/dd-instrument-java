/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classmatch;

import static datadog.instrument.classmatch.InternalMatchers.anyMatch;
import static datadog.instrument.classmatch.InternalMatchers.declaresAnnotation;
import static datadog.instrument.classmatch.InternalMatchers.declaresAnnotationOneOf;
import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

/** Fluent-API for building {@link ClassOutline} predicates. */
@FunctionalInterface
public interface ClassMatcher extends Predicate<ClassOutline> {

  /**
   * Matches classes that declare a field matching the given criteria.
   *
   * @param fieldMatcher the field matcher
   * @return matcher of classes with a matching field
   */
  static ClassMatcher declares(FieldMatcher fieldMatcher) {
    return c -> anyMatch(c.fields, fieldMatcher);
  }

  /**
   * Matches classes that declare a field matching the given criteria.
   *
   * @param accessMatcher the access matcher
   * @param fieldMatcher the field matcher
   * @return matcher of classes with a matching field
   */
  static ClassMatcher declares(IntPredicate accessMatcher, FieldMatcher fieldMatcher) {
    FieldMatcher combinedMatcher = fieldMatcher.access(accessMatcher);
    return c -> anyMatch(c.fields, combinedMatcher);
  }

  /**
   * Matches classes that declare a method matching the given criteria.
   *
   * @param methodMatcher the method matcher
   * @return matcher of classes with a matching method
   */
  static ClassMatcher declares(MethodMatcher methodMatcher) {
    return c -> anyMatch(c.methods, methodMatcher);
  }

  /**
   * Matches classes that declare a method matching the given criteria.
   *
   * @param accessMatcher the access matcher
   * @param methodMatcher the method matcher
   * @return matcher of classes with a matching method
   */
  static ClassMatcher declares(IntPredicate accessMatcher, MethodMatcher methodMatcher) {
    MethodMatcher combinedMatcher = methodMatcher.access(accessMatcher);
    return c -> anyMatch(c.methods, combinedMatcher);
  }

  /**
   * Matches classes annotated with the given type.
   *
   * @param annotationType the annotation type
   * @return matcher of classes annotated with the type
   */
  static ClassMatcher annotatedWith(String annotationType) {
    Predicate<String[]> annotationMatcher = declaresAnnotation(annotationType);
    return c -> annotationMatcher.test(c.annotations);
  }

  /**
   * Matches classes annotated with one of the given types.
   *
   * @param annotationTypes the annotation types
   * @return matcher of classes annotated with one of the types
   */
  static ClassMatcher annotatedWith(String... annotationTypes) {
    return annotatedWith(asList(annotationTypes));
  }

  /**
   * Matches classes annotated with one of the given types.
   *
   * @param annotationTypes the annotation types
   * @return matcher of classes annotated with one of the types
   */
  static ClassMatcher annotatedWith(Collection<String> annotationTypes) {
    Predicate<String[]> annotationMatcher = declaresAnnotationOneOf(annotationTypes);
    return c -> annotationMatcher.test(c.annotations);
  }

  /**
   * Conjunction of this matcher AND another.
   *
   * @param other the other matcher
   * @return conjunction of both matchers
   */
  default ClassMatcher and(ClassMatcher other) {
    return new InternalMatchers.ClassConjunction(this, other);
  }

  /**
   * Disjunction of this matcher OR another.
   *
   * @param other the other matcher
   * @return disjunction of both matchers
   */
  default ClassMatcher or(ClassMatcher other) {
    return new InternalMatchers.ClassDisjunction(this, other);
  }
}
