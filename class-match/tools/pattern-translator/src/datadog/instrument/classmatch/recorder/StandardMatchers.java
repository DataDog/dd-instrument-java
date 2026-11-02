package datadog.instrument.classmatch.recorder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/** Recording mirror of class-match's StandardMatchers. */
public final class StandardMatchers {
  private StandardMatchers() {}

  /** Name predicate: a finite set of names, or {@code null} when not expressible. */
  public static final class Names {
    final Set<String> names;

    Names(Set<String> names) {
      this.names = names;
    }
  }

  public static Names named(String name) {
    return new Names(new LinkedHashSet<>(Arrays.asList(name)));
  }

  public static Names namedOneOf(String... names) {
    return new Names(new LinkedHashSet<>(Arrays.asList(names)));
  }

  public static Names nameStartsWith(String prefix) {
    return new Names(new LinkedHashSet<>(Arrays.asList(prefix + "*")));
  }

  public static Names nameEndsWith(String suffix) {
    return new Names(new LinkedHashSet<>(Arrays.asList("*" + suffix)));
  }

  public static Names not(Names names) {
    return new Names(null);
  }

  public static TypeMatcher not(TypeMatcher type) {
    return TypeMatcher.inexpressible();
  }

  public static AccessMatcher not(AccessMatcher access) {
    return access.negate();
  }

  /** Only negations of pure access constraints are expressible, e.g. not(anyMethod().access(STATIC)). */
  public static MethodMatcher not(MethodMatcher matcher) {
    if (!matcher.fallback && matcher.alts.size() == 1) {
      Spec s = matcher.alts.get(0);
      boolean pureAccess =
          s.names == null
              && !s.regularOnly
              && s.paramCount < 0
              && s.params.isEmpty()
              && s.returnType == null
              && Integer.bitCount(s.required | s.forbidden) == 1;
      if (pureAccess) {
        Spec n = new Spec();
        n.required = s.forbidden;
        n.forbidden = s.required;
        return MethodMatcher.of(n);
      }
    }
    return new MethodMatcher(new ArrayList<>(), true);
  }

  public static ClassMatcher not(ClassMatcher matcher) {
    return ClassMatcher.FALLBACK;
  }
}
