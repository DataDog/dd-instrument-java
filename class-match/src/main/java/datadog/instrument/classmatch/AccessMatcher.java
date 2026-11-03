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

  // Every constant is a single AccessMask: (access & mask) == expected. One class keeps the
  // access call site monomorphic, loads one class instead of one per constant, and lets
  // and() merge masks exactly (see AccessMask).

  /** Matches public access. */
  AccessMatcher PUBLIC = new AccessMask(Modifier.PUBLIC, Modifier.PUBLIC);

  /** Matches private access. */
  AccessMatcher PRIVATE = new AccessMask(Modifier.PRIVATE, Modifier.PRIVATE);

  /** Matches protected access. */
  AccessMatcher PROTECTED = new AccessMask(Modifier.PROTECTED, Modifier.PROTECTED);

  /** Matches package-private access. */
  AccessMatcher PACKAGE_PRIVATE =
      new AccessMask(Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE, 0);

  /** Matches static methods/fields. */
  AccessMatcher STATIC = new AccessMask(Modifier.STATIC, Modifier.STATIC);

  /** Matches non-static methods/fields. */
  AccessMatcher INSTANCE = new AccessMask(Modifier.STATIC, 0);

  /** Matches final classes/methods/fields. */
  AccessMatcher FINAL = new AccessMask(Modifier.FINAL, Modifier.FINAL);

  /** Matches non-final classes/methods/fields. */
  AccessMatcher NON_FINAL = new AccessMask(Modifier.FINAL, 0);

  /** Matches synchronized methods. */
  AccessMatcher SYNCHRONIZED = new AccessMask(Modifier.SYNCHRONIZED, Modifier.SYNCHRONIZED);

  /** Matches varargs methods. */
  AccessMatcher VARARGS = new AccessMask(0x0080, 0x0080);

  /** Matches volatile fields. */
  AccessMatcher VOLATILE = new AccessMask(Modifier.VOLATILE, Modifier.VOLATILE);

  /** Matches transient fields. */
  AccessMatcher TRANSIENT = new AccessMask(Modifier.TRANSIENT, Modifier.TRANSIENT);

  /** Matches interface classes. */
  AccessMatcher INTERFACE = new AccessMask(Modifier.INTERFACE, Modifier.INTERFACE);

  /** Matches non-interface classes. */
  AccessMatcher CLASS = new AccessMask(Modifier.INTERFACE, 0);

  /** Matches abstract classes/methods. */
  AccessMatcher ABSTRACT = new AccessMask(Modifier.ABSTRACT, Modifier.ABSTRACT);

  /** Matches non-abstract classes/methods. */
  AccessMatcher CONCRETE = new AccessMask(Modifier.ABSTRACT, 0);

  /**
   * Conjunction of this matcher AND another.
   *
   * @param other the other matcher
   * @return conjunction of both matchers
   */
  default AccessMatcher and(AccessMatcher other) {
    return new AccessMask.And(this, other);
  }

  /**
   * Disjunction of this matcher OR another.
   *
   * @param other the other matcher
   * @return disjunction of both matchers
   */
  default AccessMatcher or(AccessMatcher other) {
    return new AccessMask.Or(this, other);
  }
}
