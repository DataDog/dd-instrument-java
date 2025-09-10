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

  static MethodMatcher method() {
    return ALL_METHODS;
  }

  static MethodMatcher method(String name) {
    return m -> name.equals(m.methodName);
  }

  static MethodMatcher method(Predicate<String> nameMatcher) {
    return m -> nameMatcher.test(m.methodName);
  }

  default MethodMatcher withAccess(IntPredicate accessMatcher) {
    return and(m -> accessMatcher.test(m.access));
  }

  default MethodMatcher withParameters(int paramCount) {
    return and(m -> m.parameterOffsets().length == paramCount);
  }

  default MethodMatcher withParameters(String... paramTypes) {
    StringBuilder buf = new StringBuilder().append('(');
    for (String paramType : paramTypes) {
      buf.append(descriptor(paramType));
    }
    String descriptorPrefix = buf.append(')').toString();
    return and(m -> m.descriptor.startsWith(descriptorPrefix));
  }

  default MethodMatcher withParameters(Class<?>... paramTypes) {
    StringBuilder buf = new StringBuilder().append('(');
    for (Class<?> paramType : paramTypes) {
      buf.append(descriptor(paramType));
    }
    String descriptorPrefix = buf.append(')').toString();
    return and(m -> m.descriptor.startsWith(descriptorPrefix));
  }

  default MethodMatcher withParameter(int paramIndex, String paramType) {
    String paramDescriptor = descriptor(paramType);
    return and(m -> declaresParameter(m, paramIndex, paramDescriptor));
  }

  default MethodMatcher withParameter(int paramIndex, Class<?> paramType) {
    String paramDescriptor = descriptor(paramType);
    return and(m -> declaresParameter(m, paramIndex, paramDescriptor));
  }

  default MethodMatcher returning(String returnType) {
    String returnDescriptor = ')' + descriptor(returnType);
    return and(m -> m.descriptor.endsWith(returnDescriptor));
  }

  default MethodMatcher returning(Class<?> returnType) {
    String returnDescriptor = ')' + descriptor(returnType);
    return and(m -> m.descriptor.endsWith(returnDescriptor));
  }

  default MethodMatcher annotatedWith(String annotation) {
    Predicate<String[]> annotationMatcher = declaresAnnotation(annotation);
    return and(m -> annotationMatcher.test(m.annotations));
  }

  default MethodMatcher annotatedWith(String... annotations) {
    return annotatedWith(asList(annotations));
  }

  default MethodMatcher annotatedWith(Collection<String> annotations) {
    Predicate<String[]> annotationMatcher = declaresAnnotationOneOf(annotations);
    return and(m -> annotationMatcher.test(m.annotations));
  }

  default MethodMatcher and(MethodMatcher other) {
    return new InternalMatchers.MethodConjunction(this, other);
  }

  default MethodMatcher or(MethodMatcher other) {
    return new InternalMatchers.MethodDisjunction(this, other);
  }
}
