/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classmatch;

import static datadog.instrument.classmatch.InternalMatchers.ALL_METHODS;
import static datadog.instrument.classmatch.InternalMatchers.CONSTRUCTOR;
import static datadog.instrument.classmatch.InternalMatchers.NO_PARAMETERS;
import static datadog.instrument.classmatch.InternalMatchers.STATIC_INITIALIZER;
import static datadog.instrument.classmatch.InternalMatchers.declaresAnnotation;
import static datadog.instrument.classmatch.InternalMatchers.declaresAnnotationOneOf;
import static datadog.instrument.classmatch.InternalMatchers.descriptor;
import static datadog.instrument.classmatch.InternalMatchers.parameterDescriptor;
import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.function.Predicate;

/** Fluent-API for building {@link MethodOutline} predicates. */
@FunctionalInterface
public interface MethodMatcher extends Predicate<MethodOutline> {

  /**
   * Matches all methods, excluding constructors and static initializers.
   *
   * @return matcher of all methods, excluding constructors and static initializers
   */
  static MethodMatcher method() {
    return ALL_METHODS;
  }

  /**
   * Matches methods with the given name.
   *
   * @param name the method name
   * @return matcher of methods with the same name
   */
  static MethodMatcher method(String name) {
    return new InternalMatchers.MethodNamed(name);
  }

  /**
   * Matches methods with names matching the given criteria.
   *
   * @param nameMatcher the method name matcher
   * @return matcher of methods with a matching name
   */
  static MethodMatcher method(Predicate<String> nameMatcher) {
    return new InternalMatchers.MethodNameMatching(nameMatcher);
  }

  /**
   * Matches constructor methods.
   *
   * @return matcher of constructor methods
   */
  static MethodMatcher constructor() {
    return CONSTRUCTOR;
  }

  /**
   * Matches static-initializer methods.
   *
   * @return matcher of static-initializer methods
   */
  static MethodMatcher staticInitializer() {
    return STATIC_INITIALIZER;
  }

  /**
   * Matches methods with access modifiers matching the given criteria.
   *
   * @param accessMatcher the access matcher
   * @return matcher of methods with matching access
   */
  default MethodMatcher access(AccessMatcher accessMatcher) {
    return and(m -> accessMatcher.test(m.access));
  }

  /**
   * Matches methods with no parameters.
   *
   * @return matcher of methods with no parameters
   */
  default MethodMatcher noParameters() {
    return and(NO_PARAMETERS);
  }

  /**
   * Matches methods with the given parameter count.
   *
   * @param paramCount the parameter count
   * @return matcher of methods with the same parameter count
   */
  default MethodMatcher parameters(int paramCount) {
    if (paramCount == 0) {
      return noParameters();
    } else {
      return and(new InternalMatchers.ParameterCount(paramCount));
    }
  }

  /**
   * Matches methods with the given parameter types.
   *
   * @param paramTypes the parameter types
   * @return matcher of methods with the same parameter types
   */
  default MethodMatcher parameters(String... paramTypes) {
    StringBuilder buf = new StringBuilder().append('(');
    for (String paramType : paramTypes) {
      buf.append(descriptor(paramType));
    }
    String prefix = buf.append(')').toString();
    return and(m -> m.descriptor.startsWith(prefix));
  }

  /**
   * Matches methods with the given parameter types.
   *
   * @param paramTypes the parameter types
   * @return matcher of methods with the same parameter types
   */
  default MethodMatcher parameters(Class<?>... paramTypes) {
    StringBuilder buf = new StringBuilder().append('(');
    for (Class<?> paramType : paramTypes) {
      buf.append(descriptor(paramType));
    }
    String prefix = buf.append(')').toString();
    return and(m -> m.descriptor.startsWith(prefix));
  }

  /**
   * Matches methods with the given parameter type at the given position.
   *
   * @param paramIndex the parameter index
   * @param paramType the parameter type
   * @return matcher of methods with the same parameter type at the same position
   */
  default MethodMatcher parameter(int paramIndex, String paramType) {
    return and(parameterDescriptor(paramIndex, descriptor(paramType)));
  }

  /**
   * Matches methods with the given parameter type at the given position.
   *
   * @param paramIndex the parameter index
   * @param paramType the parameter type
   * @return matcher of methods with the same parameter type at the same position
   */
  default MethodMatcher parameter(int paramIndex, Class<?> paramType) {
    return and(parameterDescriptor(paramIndex, descriptor(paramType)));
  }

  /**
   * Matches methods with a parameter type matching the given criteria.
   *
   * @param paramIndex the parameter index
   * @param typeMatcher the parameter type matcher
   * @return matcher of methods with a matching parameter type
   */
  default MethodMatcher parameter(int paramIndex, TypeMatcher typeMatcher) {
    return and(new InternalMatchers.ParameterType(paramIndex, typeMatcher));
  }

  /**
   * Matches methods returning the given type.
   *
   * @param returnType the return type
   * @return matcher of methods returning the same type
   */
  default MethodMatcher returning(String returnType) {
    return and(new InternalMatchers.ReturnDescriptor(descriptor(returnType)));
  }

  /**
   * Matches methods returning the given type.
   *
   * @param returnType the return type
   * @return matcher of methods returning the same type
   */
  default MethodMatcher returning(Class<?> returnType) {
    return and(new InternalMatchers.ReturnDescriptor(descriptor(returnType)));
  }

  /**
   * Matches methods returning a type matching the given criteria.
   *
   * @param typeMatcher the return type matcher
   * @return matcher of methods with a matching return type
   */
  default MethodMatcher returning(TypeMatcher typeMatcher) {
    return and(new InternalMatchers.ReturnType(typeMatcher));
  }

  /**
   * Matches methods annotated with the given type.
   *
   * @param annotationType the annotation type
   * @return matcher of methods annotated with the same type
   */
  default MethodMatcher annotatedWith(String annotationType) {
    return and(new InternalMatchers.AnnotatedMethod(declaresAnnotation(annotationType)));
  }

  /**
   * Matches methods annotated with one of the given types.
   *
   * @param annotationTypes the annotation types
   * @return matcher of methods annotated with one of the types
   */
  default MethodMatcher annotatedWith(String... annotationTypes) {
    return annotatedWith(asList(annotationTypes));
  }

  /**
   * Matches methods annotated with one of the given types.
   *
   * @param annotationTypes the annotation types
   * @return matcher of methods annotated with one of the types
   */
  default MethodMatcher annotatedWith(Collection<String> annotationTypes) {
    return and(new InternalMatchers.AnnotatedMethod(declaresAnnotationOneOf(annotationTypes)));
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
