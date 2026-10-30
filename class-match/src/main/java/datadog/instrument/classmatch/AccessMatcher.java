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

  // Use anonymous classes to reduce cold-start lambda linkage cost.

  // Keep a separate class per matcher so the JIT can specialize by matcher type; avoid
  // replacing these with a shared class holding a mask field as it slows down matching.

  /** Matches public access. */
  AccessMatcher PUBLIC =
      new AccessMatcher() {
        @Override
        public boolean test(int acc) {
          return Modifier.isPublic(acc);
        }
      };

  /** Matches private access. */
  AccessMatcher PRIVATE =
      new AccessMatcher() {
        @Override
        public boolean test(int acc) {
          return Modifier.isPrivate(acc);
        }
      };

  /** Matches protected access. */
  AccessMatcher PROTECTED =
      new AccessMatcher() {
        @Override
        public boolean test(int acc) {
          return Modifier.isProtected(acc);
        }
      };

  /** Matches package-private access. */
  AccessMatcher PACKAGE_PRIVATE =
      new AccessMatcher() {
        @Override
        public boolean test(int acc) {
          return (acc & (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE)) == 0;
        }
      };

  /** Matches static methods/fields. */
  AccessMatcher STATIC =
      new AccessMatcher() {
        @Override
        public boolean test(int acc) {
          return Modifier.isStatic(acc);
        }
      };

  /** Matches non-static methods/fields. */
  AccessMatcher INSTANCE =
      new AccessMatcher() {
        @Override
        public boolean test(int acc) {
          return (acc & Modifier.STATIC) == 0;
        }
      };

  /** Matches final classes/methods/fields. */
  AccessMatcher FINAL =
      new AccessMatcher() {
        @Override
        public boolean test(int acc) {
          return Modifier.isFinal(acc);
        }
      };

  /** Matches non-final classes/methods/fields. */
  AccessMatcher NON_FINAL =
      new AccessMatcher() {
        @Override
        public boolean test(int acc) {
          return (acc & Modifier.FINAL) == 0;
        }
      };

  /** Matches synchronized methods. */
  AccessMatcher SYNCHRONIZED =
      new AccessMatcher() {
        @Override
        public boolean test(int acc) {
          return Modifier.isSynchronized(acc);
        }
      };

  /** Matches varargs methods. */
  AccessMatcher VARARGS =
      new AccessMatcher() {
        @Override
        public boolean test(int acc) {
          return (acc & 0x0080) != 0;
        }
      };

  /** Matches volatile fields. */
  AccessMatcher VOLATILE =
      new AccessMatcher() {
        @Override
        public boolean test(int acc) {
          return Modifier.isVolatile(acc);
        }
      };

  /** Matches transient fields. */
  AccessMatcher TRANSIENT =
      new AccessMatcher() {
        @Override
        public boolean test(int acc) {
          return Modifier.isTransient(acc);
        }
      };

  /** Matches interface classes. */
  AccessMatcher INTERFACE =
      new AccessMatcher() {
        @Override
        public boolean test(int acc) {
          return Modifier.isInterface(acc);
        }
      };

  /** Matches non-interface classes. */
  AccessMatcher CLASS =
      new AccessMatcher() {
        @Override
        public boolean test(int acc) {
          return (acc & Modifier.INTERFACE) == 0;
        }
      };

  /** Matches abstract classes/methods. */
  AccessMatcher ABSTRACT =
      new AccessMatcher() {
        @Override
        public boolean test(int acc) {
          return Modifier.isAbstract(acc);
        }
      };

  /** Matches non-abstract classes/methods. */
  AccessMatcher CONCRETE =
      new AccessMatcher() {
        @Override
        public boolean test(int acc) {
          return (acc & Modifier.ABSTRACT) == 0;
        }
      };

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
