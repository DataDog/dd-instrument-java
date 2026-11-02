package datadog.instrument.classmatch.recorder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

/** Recording mirror of class-match's FieldMatcher. */
public final class FieldMatcher extends Members<FieldMatcher> {
  FieldMatcher(List<Spec> alts, boolean fallback) {
    super(alts, fallback);
  }

  @Override
  FieldMatcher make(List<Spec> alts, boolean fallback) {
    return new FieldMatcher(alts, fallback);
  }

  static FieldMatcher of(Spec s) {
    s.field = true;
    List<Spec> alts = new ArrayList<>();
    alts.add(s);
    return new FieldMatcher(alts, false);
  }

  public static FieldMatcher anyField() {
    return of(new Spec());
  }

  public static FieldMatcher field(String name) {
    Spec s = new Spec();
    s.names = new LinkedHashSet<>(Arrays.asList(name));
    return of(s);
  }

  public static FieldMatcher field(StandardMatchers.Names names) {
    if (names.names == null) {
      return new FieldMatcher(new ArrayList<>(), true);
    }
    Spec s = new Spec();
    s.names = new LinkedHashSet<>(names.names);
    return of(s);
  }

  public FieldMatcher access(AccessMatcher access) {
    return and(access.asField());
  }

  public FieldMatcher type(String type) {
    Spec s = new Spec();
    s.fieldType = Spec.descriptorOf(type);
    return and(of(s));
  }

  public FieldMatcher and(TypeMatcher type) {
    if (type.descriptors == null) {
      return fallback();
    }
    List<Spec> alts = new ArrayList<>();
    for (String d : type.descriptors) {
      Spec s = new Spec();
      s.field = true;
      s.fieldType = d;
      alts.add(s);
    }
    return and(new FieldMatcher(alts, false));
  }
}
