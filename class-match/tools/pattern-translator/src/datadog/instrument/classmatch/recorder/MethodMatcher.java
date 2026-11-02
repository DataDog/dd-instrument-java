package datadog.instrument.classmatch.recorder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

/** Recording mirror of class-match's MethodMatcher: a disjunction of member specs. */
public final class MethodMatcher extends Members<MethodMatcher> {
  MethodMatcher(List<Spec> alts, boolean fallback) {
    super(alts, fallback);
  }

  @Override
  MethodMatcher make(List<Spec> alts, boolean fallback) {
    return new MethodMatcher(alts, fallback);
  }

  static MethodMatcher of(Spec s) {
    List<Spec> alts = new ArrayList<>();
    alts.add(s);
    return new MethodMatcher(alts, false);
  }

  public static MethodMatcher anyMethod() {
    return of(new Spec());
  }

  public static MethodMatcher method() {
    Spec s = new Spec();
    s.regularOnly = true;
    return of(s);
  }

  public static MethodMatcher method(String name) {
    Spec s = new Spec();
    s.names = new LinkedHashSet<>(Arrays.asList(name));
    return of(s);
  }

  public static MethodMatcher method(StandardMatchers.Names names) {
    if (names.names == null) {
      return new MethodMatcher(new ArrayList<>(), true);
    }
    Spec s = new Spec();
    s.names = new LinkedHashSet<>(names.names);
    return of(s);
  }

  public static MethodMatcher constructor() {
    return method("<init>");
  }

  public static MethodMatcher staticInitializer() {
    return method("<clinit>");
  }

  public MethodMatcher access(AccessMatcher access) {
    return and(access.asMethod());
  }

  public MethodMatcher noParameters() {
    return parameters(0);
  }

  public MethodMatcher parameters(int count) {
    Spec s = new Spec();
    s.paramCount = count;
    return and(of(s));
  }

  public MethodMatcher parameters(String... types) {
    Spec s = new Spec();
    s.paramCount = types.length;
    for (int i = 0; i < types.length; i++) {
      s.params.put(i, Spec.descriptorOf(types[i]));
    }
    return and(of(s));
  }

  public MethodMatcher parameter(int index, String type) {
    Spec s = new Spec();
    s.params.put(index, Spec.descriptorOf(type));
    return and(of(s));
  }

  public MethodMatcher parameter(int index, TypeMatcher type) {
    if (type.descriptors == null) {
      return fallback();
    }
    List<Spec> alts = new ArrayList<>();
    for (String d : type.descriptors) {
      Spec s = new Spec();
      s.params.put(index, d);
      alts.add(s);
    }
    return and(new MethodMatcher(alts, false));
  }

  public MethodMatcher returning(String type) {
    Spec s = new Spec();
    s.returnType = Spec.descriptorOf(type);
    return and(of(s));
  }

  public MethodMatcher returning(TypeMatcher type) {
    if (type.descriptors == null) {
      return fallback();
    }
    List<Spec> alts = new ArrayList<>();
    for (String d : type.descriptors) {
      Spec s = new Spec();
      s.returnType = d;
      alts.add(s);
    }
    return and(new MethodMatcher(alts, false));
  }

  public MethodMatcher annotatedWith(String... types) {
    List<Spec> alts = new ArrayList<>();
    for (String t : types) {
      Spec s = new Spec();
      s.annotation = t.replace('.', '/');
      alts.add(s);
    }
    return and(new MethodMatcher(alts, false));
  }
}
