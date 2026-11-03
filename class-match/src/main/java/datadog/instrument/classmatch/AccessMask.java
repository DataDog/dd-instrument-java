/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classmatch;

/**
 * Access matcher as data: matches when {@code (access & mask) == expected}.
 *
 * <p>Conjunction is closed: {@code (m1, e1) AND (m2, e2)} is {@code (m1 | m2, e1 | e2)} when the
 * two agree on overlapping bits, and the constant-false {@link #NEVER} otherwise (so contradictions
 * such as {@code PUBLIC.and(PRIVATE)} are caught at construction).
 */
final class AccessMask implements AccessMatcher {
  /** Matches nothing: the result of contradictory constraints. */
  static final AccessMask NEVER = new AccessMask(0, 0, true);

  final int mask;
  final int expected;
  private final boolean never;

  AccessMask(int mask, int expected) {
    this(mask, expected, false);
  }

  private AccessMask(int mask, int expected, boolean never) {
    this.mask = mask;
    this.expected = expected;
    this.never = never;
  }

  @Override
  public boolean test(int acc) {
    return !never && (acc & mask) == expected;
  }

  @Override
  public AccessMatcher and(AccessMatcher other) {
    if (other instanceof AccessMask) {
      AccessMask o = (AccessMask) other;
      if (never || o.never || ((expected ^ o.expected) & mask & o.mask) != 0) {
        return NEVER;
      }
      return new AccessMask(mask | o.mask, expected | o.expected);
    }
    return new And(this, other);
  }

  /** Conjunction of arbitrary access matchers (named class: no lambda linkage). */
  static final class And implements AccessMatcher {
    private final AccessMatcher lhs;
    private final AccessMatcher rhs;

    And(AccessMatcher lhs, AccessMatcher rhs) {
      this.lhs = lhs;
      this.rhs = rhs;
    }

    @Override
    public boolean test(int acc) {
      return lhs.test(acc) && rhs.test(acc);
    }
  }

  /** Disjunction of arbitrary access matchers (named class: no lambda linkage). */
  static final class Or implements AccessMatcher {
    private final AccessMatcher lhs;
    private final AccessMatcher rhs;

    Or(AccessMatcher lhs, AccessMatcher rhs) {
      this.lhs = lhs;
      this.rhs = rhs;
    }

    @Override
    public boolean test(int acc) {
      return lhs.test(acc) || rhs.test(acc);
    }
  }
}
