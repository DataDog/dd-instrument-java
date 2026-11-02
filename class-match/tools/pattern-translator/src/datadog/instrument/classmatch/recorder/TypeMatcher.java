package datadog.instrument.classmatch.recorder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Recording mirror of class-match's TypeMatcher: a disjunction of exact object types. class-match
 * type matchers never match primitive or array types, and neither does an {@code Lname;} literal.
 */
public final class TypeMatcher {
  final List<String> descriptors; // null = not expressible

  private TypeMatcher(List<String> descriptors) {
    this.descriptors = descriptors;
  }

  public static TypeMatcher type(String type) {
    return new TypeMatcher(Collections.singletonList('L' + type.replace('.', '/') + ';'));
  }

  public static TypeMatcher typeOneOf(String... types) {
    List<String> out = new ArrayList<>();
    for (String t : types) {
      out.add('L' + t.replace('.', '/') + ';');
    }
    return new TypeMatcher(out);
  }

  public static TypeMatcher typeStartsWith(String prefix) {
    return inexpressible();
  }

  public static TypeMatcher typeEndsWith(String suffix) {
    return inexpressible();
  }

  public TypeMatcher or(TypeMatcher other) {
    if (descriptors == null || other.descriptors == null) {
      return inexpressible();
    }
    List<String> out = new ArrayList<>(descriptors);
    out.addAll(other.descriptors);
    return new TypeMatcher(out);
  }

  static TypeMatcher inexpressible() {
    return new TypeMatcher(null);
  }
}
