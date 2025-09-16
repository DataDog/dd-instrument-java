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

  static ClassMatcher declares(FieldMatcher fieldMatcher) {
    return c -> anyMatch(c.fields, fieldMatcher);
  }

  static ClassMatcher declares(IntPredicate accessMatcher, FieldMatcher fieldMatcher) {
    FieldMatcher combinedMatcher = fieldMatcher.withAccess(accessMatcher);
    return c -> anyMatch(c.fields, combinedMatcher);
  }

  static ClassMatcher declares(MethodMatcher methodMatcher) {
    return c -> anyMatch(c.methods, methodMatcher);
  }

  static ClassMatcher declares(IntPredicate accessMatcher, MethodMatcher methodMatcher) {
    MethodMatcher combinedMatcher = methodMatcher.withAccess(accessMatcher);
    return c -> anyMatch(c.methods, combinedMatcher);
  }

  static ClassMatcher annotatedWith(String annotation) {
    Predicate<String[]> annotationMatcher = declaresAnnotation(annotation);
    return c -> annotationMatcher.test(c.annotations);
  }

  static ClassMatcher annotatedWith(String... annotations) {
    return annotatedWith(asList(annotations));
  }

  static ClassMatcher annotatedWith(Collection<String> annotations) {
    Predicate<String[]> annotationMatcher = declaresAnnotationOneOf(annotations);
    return c -> annotationMatcher.test(c.annotations);
  }

  default ClassMatcher and(ClassMatcher other) {
    return new InternalMatchers.ClassConjunction(this, other);
  }

  default ClassMatcher or(ClassMatcher other) {
    return new InternalMatchers.ClassDisjunction(this, other);
  }
}
