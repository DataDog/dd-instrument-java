package datadog.instrument.classmatch.recorder;

import java.util.ArrayList;
import java.util.List;

/**
 * Recording mirror of class-match's ClassMatcher. A selector is expressible when it declares a
 * member matching any of a set of patterns; anything else uses the predicate fallback.
 */
public final class ClassMatcher {
  static final ClassMatcher FALLBACK = new ClassMatcher(null);

  /** Alternative member patterns, or {@code null} for the fallback. */
  final List<String> patterns;

  private ClassMatcher(List<String> patterns) {
    this.patterns = patterns;
  }

  public static ClassMatcher declares(MethodMatcher methods) {
    List<String> p = methods.toPatterns();
    return p == null ? FALLBACK : new ClassMatcher(p);
  }

  public static ClassMatcher declares(FieldMatcher fields) {
    List<String> p = fields.toPatterns();
    return p == null ? FALLBACK : new ClassMatcher(p);
  }

  public static ClassMatcher declares(AccessMatcher access, MethodMatcher methods) {
    return declares(methods.access(access));
  }

  public static ClassMatcher declares(AccessMatcher access, FieldMatcher fields) {
    return declares(fields.access(access));
  }

  public static ClassMatcher annotatedWith(String... types) {
    return FALLBACK;
  }

  /** Two declares on the same class can match different members: not one pattern. */
  public ClassMatcher and(ClassMatcher other) {
    return FALLBACK;
  }

  public ClassMatcher or(ClassMatcher other) {
    if (patterns == null || other.patterns == null) {
      return FALLBACK;
    }
    List<String> out = new ArrayList<>(patterns);
    out.addAll(other.patterns);
    return new ClassMatcher(out);
  }
}
