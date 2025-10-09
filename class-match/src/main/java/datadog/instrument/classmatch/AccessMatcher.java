/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classmatch;

import java.lang.reflect.Modifier;
import java.util.function.IntPredicate;

/**
 * Predicate for matching access modifiers defined in <a
 * href="https://docs.oracle.com/javase/specs/jvms/se24/html/jvms-4.html">class-files</a>.
 */
@FunctionalInterface
public interface AccessMatcher extends IntPredicate {

  /** Matches public access. */
  AccessMatcher PUBLIC = Modifier::isPublic;

  /** Matches private access. */
  AccessMatcher PRIVATE = Modifier::isPrivate;

  /** Matches protected access. */
  AccessMatcher PROTECTED = Modifier::isProtected;

  /** Matches package-private access. */
  AccessMatcher PACKAGE_PRIVATE =
      acc -> (acc & (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE)) == 0;

  /** Matches static methods/fields. */
  AccessMatcher STATIC = Modifier::isStatic;

  /** Matches non-static methods/fields. */
  AccessMatcher INSTANCE = acc -> (acc & Modifier.STATIC) == 0;

  /** Matches final classes/methods/fields. */
  AccessMatcher FINAL = Modifier::isFinal;

  /** Matches non-final classes/methods/fields. */
  AccessMatcher NON_FINAL = acc -> (acc & Modifier.FINAL) == 0;

  /** Matches synchronized methods. */
  AccessMatcher SYNCHRONIZED = Modifier::isSynchronized;

  /** Matches varargs methods. */
  AccessMatcher VARARGS = acc -> (acc & 0x0080) != 0;

  /** Matches volatile fields. */
  AccessMatcher VOLATILE = Modifier::isVolatile;

  /** Matches transient fields. */
  AccessMatcher TRANSIENT = Modifier::isTransient;

  /** Matches interface classes. */
  AccessMatcher INTERFACE = Modifier::isInterface;

  /** Matches non-interface classes. */
  AccessMatcher CLASS = acc -> (acc & Modifier.INTERFACE) == 0;

  /** Matches abstract classes/methods. */
  AccessMatcher ABSTRACT = Modifier::isAbstract;

  /** Matches non-abstract classes/methods. */
  AccessMatcher CONCRETE = acc -> (acc & Modifier.ABSTRACT) == 0;

  /**
   * Conjunction of this matcher AND another.
   *
   * @param other the other matcher
   * @return conjunction of both matchers
   */
  default AccessMatcher and(AccessMatcher other) {
    // simple approach as we don't expect many access-matcher unions
    return acc -> test(acc) && other.test(acc);
  }

  /**
   * Disjunction of this matcher OR another.
   *
   * @param other the other matcher
   * @return disjunction of both matchers
   */
  default AccessMatcher or(AccessMatcher other) {
    // simple approach as we don't expect many access-matcher unions
    return acc -> test(acc) || other.test(acc);
  }
}
