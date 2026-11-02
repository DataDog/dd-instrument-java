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
  AccessMatcher PUBLIC = new InternalMatchers.AccessMask(Modifier.PUBLIC);

  /** Matches private access. */
  AccessMatcher PRIVATE = new InternalMatchers.AccessMask(Modifier.PRIVATE);

  /** Matches protected access. */
  AccessMatcher PROTECTED = new InternalMatchers.AccessMask(Modifier.PROTECTED);

  /** Matches package-private access. */
  AccessMatcher PACKAGE_PRIVATE =
      new InternalMatchers.AccessMask(~(Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE));

  /** Matches static methods/fields. */
  AccessMatcher STATIC = new InternalMatchers.AccessMask(Modifier.STATIC);

  /** Matches non-static methods/fields. */
  AccessMatcher INSTANCE = new InternalMatchers.AccessMask(~Modifier.STATIC);

  /** Matches final classes/methods/fields. */
  AccessMatcher FINAL = new InternalMatchers.AccessMask(Modifier.FINAL);

  /** Matches non-final classes/methods/fields. */
  AccessMatcher NON_FINAL = new InternalMatchers.AccessMask(~Modifier.FINAL);

  /** Matches synchronized methods. */
  AccessMatcher SYNCHRONIZED = new InternalMatchers.AccessMask(Modifier.SYNCHRONIZED);

  /** Matches varargs methods. */
  AccessMatcher VARARGS = new InternalMatchers.AccessMask(0x0080);

  /** Matches volatile fields. */
  AccessMatcher VOLATILE = new InternalMatchers.AccessMask(Modifier.VOLATILE);

  /** Matches transient fields. */
  AccessMatcher TRANSIENT = new InternalMatchers.AccessMask(Modifier.TRANSIENT);

  /** Matches interface classes. */
  AccessMatcher INTERFACE = new InternalMatchers.AccessMask(Modifier.INTERFACE);

  /** Matches non-interface classes. */
  AccessMatcher CLASS = new InternalMatchers.AccessMask(~Modifier.INTERFACE);

  /** Matches abstract classes/methods. */
  AccessMatcher ABSTRACT = new InternalMatchers.AccessMask(Modifier.ABSTRACT);

  /** Matches non-abstract classes/methods. */
  AccessMatcher CONCRETE = new InternalMatchers.AccessMask(~Modifier.ABSTRACT);

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
