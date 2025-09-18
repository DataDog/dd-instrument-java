/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classmatch;

import static datadog.instrument.classmatch.InternalMatchers.ALL_METHODS;
import static datadog.instrument.classmatch.InternalMatchers.declaresAnnotation;
import static datadog.instrument.classmatch.InternalMatchers.declaresAnnotationOneOf;
import static datadog.instrument.classmatch.InternalMatchers.declaresParameter;
import static datadog.instrument.classmatch.InternalMatchers.descriptor;
import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

/** Fluent-API for building {@link MethodOutline} predicates. */
@FunctionalInterface
public interface MethodMatcher extends Predicate<MethodOutline> {

  /**
   * Matches all methods.
   *
   * @return matcher of all methods
   */
  static MethodMatcher method() {
    return ALL_METHODS;
  }

  /**
   * Matches methods with the given name.
   *
   * @param name the expected name
   * @return matcher of methods that have the same name
   */
  static MethodMatcher method(String name) {
    return m -> name.equals(m.methodName);
  }

  /**
   * Matches methods with names matching the given criteria.
   *
   * @param nameMatcher the name matcher
   * @return matcher of methods whose name matches tbe criteria
   */
  static MethodMatcher method(Predicate<String> nameMatcher) {
    return m -> nameMatcher.test(m.methodName);
  }

  /**
   * Matches constructor methods.
   *
   * @return matcher of constructor methods
   */
  static MethodMatcher constructor() {
    return m -> "<init>".equals(m.methodName);
  }

  /**
   * Matches static-initializer methods.
   *
   * @return matcher of static-initializer methods
   */
  static MethodMatcher staticInitializer() {
    return m -> "<clinit>".equals(m.methodName);
  }

  /**
   * Matches methods with access flags matching the given criteria.
   *
   * @param accessMatcher the access flag matcher
   * @return matcher of methods whose access flags match tbe criteria
   */
  default MethodMatcher withAccess(IntPredicate accessMatcher) {
    return and(m -> accessMatcher.test(m.access));
  }

  /**
   * Matches methods with the given parameter count.
   *
   * @param paramCount the expected parameter count
   * @return matcher of methods with the same parameter count
   */
  default MethodMatcher withParameters(int paramCount) {
    return and(m -> m.descriptorBoundaries().length == paramCount);
  }

  /**
   * Matches methods with the given parameter types.
   *
   * @param paramTypes the expected parameter types
   * @return matcher of methods with the same parameter types
   */
  default MethodMatcher withParameters(String... paramTypes) {
    StringBuilder buf = new StringBuilder().append('(');
    for (String paramType : paramTypes) {
      buf.append(descriptor(paramType));
    }
    String descriptorPrefix = buf.append(')').toString();
    return and(m -> m.descriptor.startsWith(descriptorPrefix));
  }

  /**
   * Matches methods with the given parameter types.
   *
   * @param paramTypes the expected parameter types
   * @return matcher of methods with the same parameter types
   */
  default MethodMatcher withParameters(Class<?>... paramTypes) {
    StringBuilder buf = new StringBuilder().append('(');
    for (Class<?> paramType : paramTypes) {
      buf.append(descriptor(paramType));
    }
    String descriptorPrefix = buf.append(')').toString();
    return and(m -> m.descriptor.startsWith(descriptorPrefix));
  }

  /**
   * Matches methods with the given parameter type at the given position.
   *
   * @param paramIndex the expected parameter index
   * @param paramType the expected parameter type
   * @return matcher of methods with the same parameter type at the same position
   */
  default MethodMatcher withParameter(int paramIndex, String paramType) {
    String paramDescriptor = descriptor(paramType);
    return and(m -> declaresParameter(m, paramIndex, paramDescriptor));
  }

  /**
   * Matches methods with the given parameter type at the given position.
   *
   * @param paramIndex the expected parameter index
   * @param paramType the expected parameter type
   * @return matcher of methods with the same parameter type at the same position
   */
  default MethodMatcher withParameter(int paramIndex, Class<?> paramType) {
    String paramDescriptor = descriptor(paramType);
    return and(m -> declaresParameter(m, paramIndex, paramDescriptor));
  }

  /**
   * Matches methods returning the given type.
   *
   * @param returnType the expected return type
   * @return matcher of methods returning the same type
   */
  default MethodMatcher returning(String returnType) {
    String returnDescriptor = ')' + descriptor(returnType);
    return and(m -> m.descriptor.endsWith(returnDescriptor));
  }

  /**
   * Matches methods returning the given type.
   *
   * @param returnType the expected return type
   * @return matcher of methods returning the same type
   */
  default MethodMatcher returning(Class<?> returnType) {
    String returnDescriptor = ')' + descriptor(returnType);
    return and(m -> m.descriptor.endsWith(returnDescriptor));
  }

  /**
   * Matches methods annotated with the given type.
   *
   * @param annotation the expected annotation type
   * @return matcher of methods annotated with the same type
   */
  default MethodMatcher annotatedWith(String annotation) {
    Predicate<String[]> annotationMatcher = declaresAnnotation(annotation);
    return and(m -> annotationMatcher.test(m.annotations));
  }

  /**
   * Matches methods annotated with one of the given types.
   *
   * @param annotations the expected annotation types
   * @return matcher of methods annotated with one of the types
   */
  default MethodMatcher annotatedWith(String... annotations) {
    return annotatedWith(asList(annotations));
  }

  /**
   * Matches methods annotated with one of the given types.
   *
   * @param annotations the expected annotation types
   * @return matcher of methods annotated with one of the types
   */
  default MethodMatcher annotatedWith(Collection<String> annotations) {
    Predicate<String[]> annotationMatcher = declaresAnnotationOneOf(annotations);
    return and(m -> annotationMatcher.test(m.annotations));
  }

  /**
   * Conjunction of this matcher AND another.
   *
   * @param other the other matcher
   * @return conjunction of both matchers
   */
  default MethodMatcher and(MethodMatcher other) {
    return new InternalMatchers.MethodConjunction(this, other);
  }

  /**
   * Disjunction of this matcher OR another.
   *
   * @param other the other matcher
   * @return disjunction of both matchers
   */
  default MethodMatcher or(MethodMatcher other) {
    return new InternalMatchers.MethodDisjunction(this, other);
  }
}
