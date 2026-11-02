package datadog.instrument.classmatch.recorder;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/** Recording mirror of class-match's AccessMatcher: a disjunction of (required, forbidden) masks. */
public final class AccessMatcher {
  private static final int VISIBILITY = Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE;

  public static final AccessMatcher PUBLIC = required(Modifier.PUBLIC);
  public static final AccessMatcher PRIVATE = required(Modifier.PRIVATE);
  public static final AccessMatcher PROTECTED = required(Modifier.PROTECTED);
  public static final AccessMatcher PACKAGE_PRIVATE = forbidden(VISIBILITY);
  public static final AccessMatcher STATIC = required(Modifier.STATIC);
  public static final AccessMatcher INSTANCE = forbidden(Modifier.STATIC);
  public static final AccessMatcher FINAL = required(Modifier.FINAL);
  public static final AccessMatcher NON_FINAL = forbidden(Modifier.FINAL);
  public static final AccessMatcher SYNCHRONIZED = required(Modifier.SYNCHRONIZED);
  public static final AccessMatcher VOLATILE = required(Modifier.VOLATILE);
  public static final AccessMatcher TRANSIENT = required(Modifier.TRANSIENT);
  public static final AccessMatcher ABSTRACT = required(Modifier.ABSTRACT);
  public static final AccessMatcher CONCRETE = forbidden(Modifier.ABSTRACT);

  final List<int[]> alts; // each {required, forbidden}; null = not expressible

  private AccessMatcher(List<int[]> alts) {
    this.alts = alts;
  }

  /** Stand-in for ad hoc lambdas of the form {@code acc -> (acc & bit) != 0}. */
  public static AccessMatcher bit(int bit) {
    return required(bit);
  }

  private static AccessMatcher required(int mask) {
    List<int[]> alts = new ArrayList<>();
    alts.add(new int[] {mask, 0});
    return new AccessMatcher(alts);
  }

  private static AccessMatcher forbidden(int mask) {
    List<int[]> alts = new ArrayList<>();
    alts.add(new int[] {0, mask});
    return new AccessMatcher(alts);
  }

  public AccessMatcher and(AccessMatcher other) {
    if (alts == null || other.alts == null) return new AccessMatcher(null);
    List<int[]> out = new ArrayList<>();
    for (int[] a : alts) {
      for (int[] b : other.alts) {
        out.add(new int[] {a[0] | b[0], a[1] | b[1]});
      }
    }
    return new AccessMatcher(out);
  }

  public AccessMatcher or(AccessMatcher other) {
    if (alts == null || other.alts == null) return new AccessMatcher(null);
    List<int[]> out = new ArrayList<>(alts);
    out.addAll(other.alts);
    return new AccessMatcher(out);
  }

  /** Negation, only for a single mask with a single bit (e.g. not(STATIC)). */
  AccessMatcher negate() {
    if (alts != null && alts.size() == 1) {
      int[] a = alts.get(0);
      if (a[1] == 0 && Integer.bitCount(a[0]) == 1) {
        return forbidden(a[0]);
      }
      if (a[0] == 0 && Integer.bitCount(a[1]) == 1) {
        return required(a[1]);
      }
    }
    return new AccessMatcher(null);
  }

  MethodMatcher asMethod() {
    if (alts == null) return new MethodMatcher(new ArrayList<>(), true);
    List<Spec> specs = new ArrayList<>();
    for (int[] a : alts) {
      Spec s = new Spec();
      s.required = a[0];
      s.forbidden = a[1];
      specs.add(s);
    }
    return new MethodMatcher(specs, false);
  }

  FieldMatcher asField() {
    if (alts == null) return new FieldMatcher(new ArrayList<>(), true);
    List<Spec> specs = new ArrayList<>();
    for (int[] a : alts) {
      Spec s = new Spec();
      s.field = true;
      s.required = a[0];
      s.forbidden = a[1];
      specs.add(s);
    }
    return new FieldMatcher(specs, false);
  }
}
