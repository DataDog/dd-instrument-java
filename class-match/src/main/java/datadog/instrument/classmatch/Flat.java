/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classmatch;

import java.util.function.Predicate;

/**
 * Prototype entry points mirroring {@link MethodMatcher}'s static factories (and {@code not}), but
 * producing {@link FlatMethodMatcher}s. Switching a selector file to these is an import change.
 */
public final class Flat {
  private Flat() {}

  /** Any method, including constructors and static initializers (Byte Buddy semantics). */
  public static MethodMatcher anyMethod() {
    return FlatMethodMatcher.ANY;
  }

  /** Any regular method, excluding constructors and static initializers. */
  public static MethodMatcher method() {
    return FlatMethodMatcher.REGULAR;
  }

  public static MethodMatcher method(String name) {
    return FlatMethodMatcher.named(name);
  }

  public static MethodMatcher method(Predicate<String> nameMatcher) {
    String[] names = FlatMethodMatcher.exactNames(nameMatcher);
    return names != null
        ? FlatMethodMatcher.named(names)
        : FlatMethodMatcher.residualOnly(new FlatMethodMatcher.NameResidual(nameMatcher));
  }

  public static MethodMatcher constructor() {
    return FlatMethodMatcher.named(ClassFile.CONSTRUCTOR);
  }

  public static MethodMatcher staticInitializer() {
    return FlatMethodMatcher.named(ClassFile.STATIC_INITIALIZER);
  }

  /** Flattens negated single-bit access constraints; anything else stays a predicate. */
  public static MethodMatcher not(MethodMatcher matcher) {
    if (matcher instanceof FlatMethodMatcher) {
      FlatMethodMatcher negated = ((FlatMethodMatcher) matcher).negateAccessOnly();
      if (negated != null) {
        return negated;
      }
    }
    return StandardMatchers.not(matcher);
  }

  public static TypeMatcher not(TypeMatcher matcher) {
    return StandardMatchers.not(matcher);
  }

  public static ClassMatcher not(ClassMatcher matcher) {
    return StandardMatchers.not(matcher);
  }

  public static FieldMatcher not(FieldMatcher matcher) {
    return StandardMatchers.not(matcher);
  }

  public static AccessMatcher not(AccessMatcher matcher) {
    return StandardMatchers.not(matcher);
  }

  public static <T> Predicate<T> not(Predicate<T> predicate) {
    return StandardMatchers.not(predicate);
  }
}
