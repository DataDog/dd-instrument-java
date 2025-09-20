/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classmatch;

import static datadog.instrument.classmatch.InternalMatchers.internalName;
import static datadog.instrument.classmatch.InternalMatchers.internalNames;
import static java.util.Arrays.asList;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

/** Standard matchers, part of the public API. */
public final class StandardMatchers {

  /** Matches public access. */
  public static final IntPredicate PUBLIC = Modifier::isPublic;

  /** Matches private access. */
  public static final IntPredicate PRIVATE = Modifier::isPrivate;

  /** Matches protected access. */
  public static final IntPredicate PROTECTED = Modifier::isProtected;

  /** Matches static access. */
  public static final IntPredicate STATIC = Modifier::isStatic;

  /** Matches final classes/methods/fields. */
  public static final IntPredicate FINAL = Modifier::isFinal;

  /** Matches synchronized methods. */
  public static final IntPredicate SYNCHRONIZED = Modifier::isSynchronized;

  /** Matches volatile fields. */
  public static final IntPredicate VOLATILE = Modifier::isVolatile;

  /** Matches transient fields. */
  public static final IntPredicate TRANSIENT = Modifier::isTransient;

  /** Matches native methods. */
  public static final IntPredicate NATIVE = Modifier::isNative;

  /** Matches interface classes. */
  public static final IntPredicate INTERFACE = Modifier::isInterface;

  /** Matches abstract classes. */
  public static final IntPredicate ABSTRACT = Modifier::isAbstract;

  private StandardMatchers() {}

  /**
   * Syntactic sugar around {@link Predicate#negate()}.
   *
   * @param <T> the predicate's target type
   * @param predicate the predicate to negate
   * @return negated predicate
   */
  public static <T> Predicate<T> not(Predicate<T> predicate) {
    return predicate.negate();
  }

  /**
   * Matches when the target has the given name.
   *
   * @param name the expected name
   * @return {@code true} if the target has the name; otherwise {@code false}
   */
  public static Predicate<String> named(String name) {
    return internalName(name)::equals;
  }

  /**
   * Matches when the target has one of the given names.
   *
   * @param names the expected names
   * @return {@code true} if the target has one of the names; otherwise {@code false}
   */
  public static Predicate<String> namedOneOf(String... names) {
    return internalNames(asList(names))::contains;
  }

  /**
   * Matches when the target has one of the given names.
   *
   * @param names the expected names
   * @return {@code true} if the target has one of the names; otherwise {@code false}
   */
  public static Predicate<String> namedOneOf(Collection<String> names) {
    return internalNames(names)::contains;
  }

  /**
   * Matches when the target's name starts with the given prefix.
   *
   * @param prefix the expected prefix
   * @return {@code true} if the target's name starts with the prefix; otherwise {@code false}
   */
  public static Predicate<String> nameStartsWith(String prefix) {
    String internalPrefix = internalName(prefix);
    return s -> s.startsWith(internalPrefix);
  }

  /**
   * Matches when the target's name ends with the given suffix.
   *
   * @param suffix the expected suffix
   * @return {@code true} if the target's name ends with the suffix; otherwise {@code false}
   */
  public static Predicate<String> nameEndsWith(String suffix) {
    String internalSuffix = internalName(suffix);
    return s -> s.endsWith(internalSuffix);
  }
}
