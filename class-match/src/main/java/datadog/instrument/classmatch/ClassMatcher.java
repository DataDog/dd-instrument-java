/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classmatch;

import static datadog.instrument.classmatch.InternalMatchers.declaresAnnotation;
import static datadog.instrument.classmatch.InternalMatchers.declaresAnnotationOneOf;
import static java.util.Arrays.asList;

import java.util.Collection;
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
    return new InternalMatchers.DeclaresField(fieldMatcher);
  }

  /**
   * Matches classes that declare a field matching the given criteria.
   *
   * @param accessMatcher the access matcher
   * @param fieldMatcher the field matcher
   * @return matcher of classes with a matching field
   */
  static ClassMatcher declares(AccessMatcher accessMatcher, FieldMatcher fieldMatcher) {
    return new InternalMatchers.DeclaresField(fieldMatcher.access(accessMatcher));
  }

  /**
   * Matches classes that declare a method matching the given criteria.
   *
   * @param methodMatcher the method matcher
   * @return matcher of classes with a matching method
   */
  static ClassMatcher declares(MethodMatcher methodMatcher) {
    return new InternalMatchers.DeclaresMethod(methodMatcher);
  }

  /**
   * Matches classes that declare a method matching the given criteria.
   *
   * @param accessMatcher the access matcher
   * @param methodMatcher the method matcher
   * @return matcher of classes with a matching method
   */
  static ClassMatcher declares(AccessMatcher accessMatcher, MethodMatcher methodMatcher) {
    return new InternalMatchers.DeclaresMethod(methodMatcher.access(accessMatcher));
  }

  /**
   * Matches classes annotated with the given type.
   *
   * @param annotationType the annotation type
   * @return matcher of classes annotated with the type
   */
  static ClassMatcher annotatedWith(String annotationType) {
    return new InternalMatchers.AnnotatedClass(declaresAnnotation(annotationType));
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
    return new InternalMatchers.AnnotatedClass(declaresAnnotationOneOf(annotationTypes));
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
