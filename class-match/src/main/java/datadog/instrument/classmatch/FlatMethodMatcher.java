/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classmatch;

import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;

/**
 * Flattened method matcher: one macro step of fixed checks instead of a chain of predicates.
 *
 * <ol>
 *   <li>name: exact names (or any regular method, or anything)
 *   <li>access flags: {@code (access & mask) == expected}
 *   <li>descriptor: parameter count, per-index types and return type, compiled lazily to a {@link
 *       MemberPattern} on first use (usually a single String operation)
 *   <li>everything else: at most one residual predicate
 * </ol>
 *
 * <p>Fluent steps copy-and-modify a single slot, so building a chain allocates one small matcher
 * per step and links no lambdas. {@link #and} merges slots when both sides are flat; constraints
 * that can't be flattened (annotations, type prefixes, custom predicates) go to the residual.
 */
final class FlatMethodMatcher implements MethodMatcher {
  private static final long OPAQUE = -1L;

  @Nullable final String[] names; // null = no name constraint
  final boolean regularOnly; // excludes <init>/<clinit>
  final int mask;
  final int expected;
  final int paramCount; // -1 = unknown
  @Nullable final String[] params; // index -> descriptor, null entry = hole
  @Nullable final String returnType;
  @Nullable final MethodMatcher residual;
  final boolean unsat;

  private MemberPattern descriptorPattern; // compiled lazily from the descriptor slots

  private FlatMethodMatcher(
      @Nullable String[] names,
      boolean regularOnly,
      int mask,
      int expected,
      int paramCount,
      @Nullable String[] params,
      @Nullable String returnType,
      @Nullable MethodMatcher residual,
      boolean unsat) {
    this.names = names;
    this.regularOnly = regularOnly;
    this.mask = mask;
    this.expected = expected;
    this.paramCount = paramCount;
    this.params = params;
    this.returnType = returnType;
    this.residual = residual;
    this.unsat = unsat;
  }

  static final FlatMethodMatcher ANY =
      new FlatMethodMatcher(null, false, 0, 0, -1, null, null, null, false);
  static final FlatMethodMatcher REGULAR =
      new FlatMethodMatcher(null, true, 0, 0, -1, null, null, null, false);

  static FlatMethodMatcher named(String... names) {
    return new FlatMethodMatcher(names, false, 0, 0, -1, null, null, null, false);
  }

  static FlatMethodMatcher residualOnly(MethodMatcher residual) {
    return new FlatMethodMatcher(null, false, 0, 0, -1, null, null, residual, false);
  }

  // single-slot copies: one allocation per fluent step

  private FlatMethodMatcher withAccess(int m, int e) {
    boolean contradiction = ((expected ^ e) & mask & m) != 0;
    return new FlatMethodMatcher(
        names,
        regularOnly,
        mask | m,
        expected | e,
        paramCount,
        params,
        returnType,
        residual,
        unsat || contradiction);
  }

  private FlatMethodMatcher withCount(int count) {
    boolean contradiction =
        (paramCount >= 0 && paramCount != count) || (params != null && params.length > count);
    return new FlatMethodMatcher(
        names,
        regularOnly,
        mask,
        expected,
        count,
        params,
        returnType,
        residual,
        unsat || contradiction);
  }

  private FlatMethodMatcher withParam(int index, String descriptor) {
    String[] p = params;
    boolean contradiction = paramCount >= 0 && index >= paramCount;
    if (p == null || index >= p.length) {
      String[] grown = new String[index + 1];
      if (p != null) {
        System.arraycopy(p, 0, grown, 0, p.length);
      }
      p = grown;
    } else {
      String prev = p[index];
      if (prev != null) {
        contradiction |= !prev.equals(descriptor);
        if (prev.equals(descriptor)) {
          return this;
        }
      }
      p = p.clone();
    }
    p[index] = descriptor;
    return new FlatMethodMatcher(
        names,
        regularOnly,
        mask,
        expected,
        paramCount,
        p,
        returnType,
        residual,
        unsat || contradiction);
  }

  private FlatMethodMatcher withParams(String[] descriptors) {
    FlatMethodMatcher m = withCount(descriptors.length);
    String[] p = m.params;
    boolean contradiction = false;
    if (p != null) {
      for (int i = 0; i < Math.min(p.length, descriptors.length); i++) {
        contradiction |= p[i] != null && !p[i].equals(descriptors[i]);
      }
    }
    return new FlatMethodMatcher(
        m.names,
        m.regularOnly,
        m.mask,
        m.expected,
        m.paramCount,
        descriptors,
        m.returnType,
        m.residual,
        m.unsat || contradiction);
  }

  private FlatMethodMatcher withReturn(String descriptor) {
    boolean contradiction = returnType != null && !returnType.equals(descriptor);
    return new FlatMethodMatcher(
        names,
        regularOnly,
        mask,
        expected,
        paramCount,
        params,
        descriptor,
        residual,
        unsat || contradiction);
  }

  private FlatMethodMatcher withResidual(MethodMatcher other) {
    MethodMatcher r =
        residual == null ? other : new InternalMatchers.MethodConjunction(residual, other);
    return new FlatMethodMatcher(
        names, regularOnly, mask, expected, paramCount, params, returnType, r, unsat);
  }

  /** Mask-based access matchers as {@code mask << 32 | expected}; {@link #OPAQUE} otherwise. */
  static long maskOf(AccessMatcher a) {
    if (a instanceof AccessMask && a != AccessMask.NEVER) {
      AccessMask m = (AccessMask) a;
      return ((long) m.mask << 32) | (m.expected & 0xFFFFFFFFL);
    }
    return OPAQUE;
  }

  /** Negation of a flat matcher holding only a single-bit access constraint, else null. */
  @Nullable
  FlatMethodMatcher negateAccessOnly() {
    if (names == null
        && !regularOnly
        && paramCount < 0
        && params == null
        && returnType == null
        && residual == null
        && Integer.bitCount(mask) == 1) {
      return new FlatMethodMatcher(null, false, mask, mask ^ expected, -1, null, null, null, unsat);
    }
    return null;
  }

  // merging

  @Override
  public MethodMatcher and(MethodMatcher other) {
    return other instanceof FlatMethodMatcher
        ? merge((FlatMethodMatcher) other)
        : withResidual(other);
  }

  FlatMethodMatcher merge(FlatMethodMatcher o) {
    FlatMethodMatcher m = this;
    if (o.names != null || (o.regularOnly && !regularOnly)) {
      m = m.withNames(o.names, o.regularOnly);
    }
    if (o.mask != 0) {
      m = m.withAccess(o.mask, o.expected);
    }
    if (o.paramCount >= 0) {
      m = m.withCount(o.paramCount);
    }
    if (o.params != null) {
      for (int i = 0; i < o.params.length; i++) {
        if (o.params[i] != null) {
          m = m.withParam(i, o.params[i]);
        }
      }
    }
    if (o.returnType != null) {
      m = m.withReturn(o.returnType);
    }
    if (o.residual != null) {
      m = m.withResidual(o.residual);
    }
    if (o.unsat && !m.unsat) {
      m =
          new FlatMethodMatcher(
              m.names,
              m.regularOnly,
              m.mask,
              m.expected,
              m.paramCount,
              m.params,
              m.returnType,
              m.residual,
              true);
    }
    return m;
  }

  /** Intersects names and applies the regular-method constraint, without collections. */
  private FlatMethodMatcher withNames(@Nullable String[] other, boolean otherRegular) {
    boolean regular = regularOnly || otherRegular;
    String[] base = names;
    if (other != null) {
      if (base == null) {
        base = other;
      } else {
        String[] both = new String[Math.min(base.length, other.length)];
        int n = 0;
        for (String a : base) {
          for (String b : other) {
            if (a.equals(b)) {
              both[n++] = a;
              break;
            }
          }
        }
        base = n == both.length ? both : copyOf(both, n);
      }
    }
    if (base != null && regular) {
      int keep = 0;
      for (String s : base) {
        keep += s.charAt(0) != '<' ? 1 : 0;
      }
      if (keep != base.length) {
        String[] filtered = new String[keep];
        int n = 0;
        for (String s : base) {
          if (s.charAt(0) != '<') {
            filtered[n++] = s;
          }
        }
        base = filtered;
      }
    }
    boolean empty = base != null && base.length == 0;
    return new FlatMethodMatcher(
        base, regular, mask, expected, paramCount, params, returnType, residual, unsat || empty);
  }

  private static String[] copyOf(String[] a, int n) {
    String[] r = new String[n];
    System.arraycopy(a, 0, r, 0, n);
    return r;
  }

  // fluent overrides: each step updates one slot instead of nesting a predicate

  @Override
  public MethodMatcher access(AccessMatcher accessMatcher) {
    long m = maskOf(accessMatcher);
    return m != OPAQUE
        ? withAccess((int) (m >>> 32), (int) m)
        : withResidual(new AccessResidual(accessMatcher));
  }

  @Override
  public MethodMatcher noParameters() {
    return withCount(0);
  }

  @Override
  public MethodMatcher parameters(int paramCount) {
    return withCount(paramCount);
  }

  @Override
  public MethodMatcher parameters(String... paramTypes) {
    String[] d = new String[paramTypes.length];
    for (int i = 0; i < d.length; i++) {
      d[i] = InternalMatchers.descriptor(paramTypes[i]);
    }
    return withParams(d);
  }

  @Override
  public MethodMatcher parameters(Class<?>... paramTypes) {
    String[] d = new String[paramTypes.length];
    for (int i = 0; i < d.length; i++) {
      d[i] = InternalMatchers.descriptor(paramTypes[i]);
    }
    return withParams(d);
  }

  @Override
  public MethodMatcher parameter(int paramIndex, String paramType) {
    return withParam(paramIndex, InternalMatchers.descriptor(paramType));
  }

  @Override
  public MethodMatcher parameter(int paramIndex, Class<?> paramType) {
    return withParam(paramIndex, InternalMatchers.descriptor(paramType));
  }

  @Override
  public MethodMatcher parameter(int paramIndex, TypeMatcher typeMatcher) {
    if (typeMatcher instanceof InternalMatchers.TypeNamed) {
      // TypeMatcher only matches object types, as does an Lname; literal
      return withParam(paramIndex, 'L' + ((InternalMatchers.TypeNamed) typeMatcher).name + ';');
    }
    return withResidual(new InternalMatchers.ParameterType(paramIndex, typeMatcher));
  }

  @Override
  public MethodMatcher returning(String returnType) {
    return withReturn(InternalMatchers.descriptor(returnType));
  }

  @Override
  public MethodMatcher returning(Class<?> returnType) {
    return withReturn(InternalMatchers.descriptor(returnType));
  }

  @Override
  public MethodMatcher returning(TypeMatcher typeMatcher) {
    if (typeMatcher instanceof InternalMatchers.TypeNamed) {
      return withReturn('L' + ((InternalMatchers.TypeNamed) typeMatcher).name + ';');
    }
    return withResidual(new InternalMatchers.ReturnType(typeMatcher));
  }

  // matching

  private MemberPattern descriptorPattern() {
    MemberPattern p = descriptorPattern;
    if (p == null) {
      StringBuilder b = new StringBuilder("m");
      String[] ps = params;
      if (paramCount >= 0 || ps != null) {
        int n = paramCount >= 0 ? paramCount : ps.length;
        b.append('(');
        for (int i = 0; i < n; i++) {
          String d = ps != null && i < ps.length ? ps[i] : null;
          b.append(d == null ? "?" : d);
        }
        if (paramCount < 0) {
          b.append('*');
        }
        b.append(')').append(returnType == null ? "?" : returnType);
      } else if (returnType != null) {
        b.append("(*)").append(returnType);
      }
      descriptorPattern = p = MemberPattern.compile(b.toString());
    }
    return p;
  }

  /** Name check alone, so declaration loops can run it ahead of the other slots. */
  boolean nameMatches(String name) {
    String[] ns = names;
    if (ns != null) {
      for (String n : ns) {
        if (n.equals(name)) {
          return true;
        }
      }
      return false;
    }
    return !regularOnly || name.charAt(0) != '<';
  }

  @Override
  public boolean test(MethodOutline m) {
    return !unsat
        && nameMatches(m.methodName)
        && (m.access & mask) == expected
        && descriptorPattern().matches(0, m.descriptor)
        && (residual == null || residual.test(m));
  }

  /** Extracts exact names from known name predicates, else null. */
  @Nullable
  static String[] exactNames(Predicate<String> names) {
    if (names instanceof InternalMatchers.NameInSet) {
      Set<String> set = ((InternalMatchers.NameInSet) names).names;
      return set.toArray(new String[0]);
    }
    return null;
  }

  /** Residual for opaque access matchers (named class: no lambda linkage). */
  static final class AccessResidual implements MethodMatcher {
    private final AccessMatcher access;

    AccessResidual(AccessMatcher access) {
      this.access = access;
    }

    @Override
    public boolean test(MethodOutline m) {
      return access.test(m.access);
    }
  }

  /** Residual for opaque name predicates (named class: no lambda linkage). */
  static final class NameResidual implements MethodMatcher {
    private final Predicate<String> names;

    NameResidual(Predicate<String> names) {
      this.names = names;
    }

    @Override
    public boolean test(MethodOutline m) {
      return names.test(m.methodName);
    }
  }
}
